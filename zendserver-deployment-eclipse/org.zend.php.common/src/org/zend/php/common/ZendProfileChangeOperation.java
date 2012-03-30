package org.zend.php.common;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.p2.operations.IFailedStatusEvaluator;
import org.eclipse.equinox.internal.p2.operations.IStatusCodes;
import org.eclipse.equinox.internal.p2.operations.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.operations.PlannerResolutionJob;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProfileModificationJob;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.planner.ProfileInclusionRules;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

/**
 * This class is based on
 * org.eclipse.equinox.p2.operations.ProfileChangeOperation, as
 * ProfileChangeOperation not intended to be subclassed by clients.
 * 
 * ComputeProfileChangeRequest() method has been modified to enable
 * installation/uninstallation in one step.
 * 
 * @see org.eclipse.equinox.p2.operations.ProfileChangeOperation
 */
public class ZendProfileChangeOperation {

	ProvisioningSession session;
	String profileId;
	ProvisioningContext context;
	MultiStatus noChangeRequest;
	PlannerResolutionJob job;
	ProfileChangeRequest request;
	Collection<IInstallableUnit> toInstall;
	Collection<IInstallableUnit> toUninstall;
	private String provisioningJobName;

	/**
	 * Create an operation using the provided provisioning session. Unless
	 * otherwise specified by the client, the operation is performed on the
	 * currently running profile.
	 * 
	 * @param session
	 *            the provisioning session providing the services
	 */
	protected ZendProfileChangeOperation(ProvisioningSession session,
			Collection<IInstallableUnit> toInstall,
			Collection<IInstallableUnit> toUninstall, String jobName) {
		this.session = session;
		this.profileId = IProfileRegistry.SELF;
		this.context = new ProvisioningContext(session.getProvisioningAgent());
		this.toInstall = toInstall;
		this.toUninstall = toUninstall;
		this.provisioningJobName = jobName != null ? jobName : Messages.ModifyOperation_InstallJobName;
	}

	/**
	 * Resolve the operation in the current thread using the specified progress
	 * monitor. Return a status describing the result of the resolution.
	 * 
	 * @param monitor
	 *            the progress monitor to use
	 * @return a status describing the resolution results
	 */
	public final IStatus resolveModal(IProgressMonitor monitor) {
		if (monitor == null)
			monitor = new NullProgressMonitor();
		prepareToResolve();
		makeResolveJob(monitor);
		if (job != null) {
			IStatus status = job.runModal(monitor);
//			Map<IInstallableUnit, RequestStatus> sideEffects = ((PlannerStatus)status).getRequestSideEffects();
//			if(sideEffects!= null && !sideEffects.isEmpty()){
//				Display.getDefault().syncExec(new Runnable() {
//					
//					public void run() {
//						// TODO Auto-generated method stub
//						
//					}
//				});
//			}
			if (status.getSeverity() == IStatus.CANCEL)
				return Status.CANCEL_STATUS;
		}
		// For anything other than cancellation, we examine the artifacts of the
		// resolution and come
		// up with an overall summary.
		return getResolutionResult();

	}

	/**
	 * Set the id of the profile that will be modified by this operation.
	 * 
	 * @param id
	 *            the profile id
	 */
	public void setProfileId(String id) {
		this.profileId = id;
	}

	/**
	 * Return a job that can be used to resolve this operation in the
	 * background.
	 * 
	 * @param monitor
	 *            a progress monitor that should be used to report the job's
	 *            progress in addition to the standard job progress reporting.
	 *            Can be <code>null</code>. If provided, this monitor will be
	 *            called from a background thread.
	 * 
	 * @return a job that can be scheduled to perform the provisioning
	 *         operation.
	 */
	public final ProvisioningJob getResolveJob(IProgressMonitor monitor) {
		SubMonitor mon = SubMonitor.convert(monitor,
				Messages.ProfileChangeOperation_ResolveTaskName, 1000);
		prepareToResolve();
		makeResolveJob(mon.newChild(100));
		job.setAdditionalProgressMonitor(mon.newChild(900));
		return job;
	}

	/**
	 * Perform any processing that must occur just before resolving this
	 * operation.
	 */
	protected void prepareToResolve() {
		// default is to do nothing
	}

	void makeResolveJob(IProgressMonitor monitor) {
		noChangeRequest = PlanAnalyzer.getProfileChangeAlteredStatus();
		if (session.hasScheduledOperationsFor(profileId)) {
			noChangeRequest.add(PlanAnalyzer.getStatus(
					IStatusCodes.OPERATION_ALREADY_IN_PROGRESS, null));
		} else {
			computeProfileChangeRequest(noChangeRequest, monitor);
		}
		if (request == null) {
			if (noChangeRequest.getChildren().length == 0)
				// No explanation for failure was provided. It shouldn't happen,
				// but...
				noChangeRequest = new MultiStatus(Activator.PLUGIN_ID,
						IStatusCodes.UNEXPECTED_NOTHING_TO_DO,
						new IStatus[] { PlanAnalyzer.getStatus(
								IStatusCodes.UNEXPECTED_NOTHING_TO_DO, null) },
						Messages.ProfileChangeOperation_NoProfileChangeRequest,
						null);
			return;
		}
		createPlannerResolutionJob();
	}

	private void createPlannerResolutionJob() {
		job = new PlannerResolutionJob(getResolveJobName(), session, profileId,
				request, getFirstPassProvisioningContext(),
				getSecondPassEvaluator(), noChangeRequest);
	}

	protected String getResolveJobName() {
		return Messages.ModifyOperation_ResolveJobName;
	}

	protected String getProvisioningJobName() {
		return provisioningJobName;

	}

	/**
	 * Return a status indicating the result of resolving this operation. A
	 * <code>null</code> return indicates that resolving has not occurred yet.
	 * 
	 * @return the status of the resolution, or <code>null</code> if resolution
	 *         has not yet occurred.
	 */
	public IStatus getResolutionResult() {
		if (request == null) {
			if (noChangeRequest != null) {
				// If there is only one child message, use the specific message
				if (noChangeRequest.getChildren().length == 1)
					return noChangeRequest.getChildren()[0];
				return noChangeRequest;
			}
			return null;
		}
		if (job != null && job.getResolutionResult() != null)
			return job.getResolutionResult().getSummaryStatus();
		return null;
	}

	/**
	 * Return a string that can be used to describe the results of the
	 * resolution to a client.
	 * 
	 * @return a string describing the resolution details, or <code>null</code>
	 *         if the operation has not been resolved.
	 */
	public String getResolutionDetails() {
		if (job != null && job.getResolutionResult() != null)
			return job.getResolutionResult().getSummaryReport();
		// We couldn't resolve, but we have some status describing
		// why there is no profile change request.
		IStatus result = getResolutionResult();
		if (result != null)
			return result.getMessage();
		return null;

	}

	/**
	 * Return a string that describes the specific resolution results related to
	 * the supplied {@link IInstallableUnit}.
	 * 
	 * @param iu
	 *            the IInstallableUnit for which resolution details are
	 *            requested
	 * 
	 * @return a string describing the results for the installable unit, or
	 *         <code>null</code> if there are no specific results available for
	 *         the installable unit.
	 */
	public String getResolutionDetails(IInstallableUnit iu) {
		if (job != null && job.getResolutionResult() != null)
			return job.getResolutionResult().getDetailedReport(
					new IInstallableUnit[] { iu });
		return null;

	}

	/**
	 * Return the provisioning plan obtained by resolving the receiver.
	 * 
	 * @return the provisioning plan. This may be <code>null</code> if the
	 *         operation has not been resolved, or if a plan could not be
	 *         obtained when attempting to resolve. If the plan is null and the
	 *         operation has been resolved, then the resolution result will
	 *         explain the problem.
	 * 
	 * @see #hasResolved()
	 * @see #getResolutionResult()
	 */
	public IProvisioningPlan getProvisioningPlan() {
		if (job != null)
			return job.getProvisioningPlan();
		return null;
	}

	/**
	 * Return the profile change request that describes the receiver.
	 * 
	 * @return the profile change request. This may be <code>null</code> if the
	 *         operation has not been resolved, or if a profile change request
	 *         could not be assembled given the operation's state. If the
	 *         profile change request is null and the operation has been
	 *         resolved, the the resolution result will explain the problem.
	 * 
	 * @see #hasResolved()
	 * @see #getResolutionResult()
	 * @since 2.1
	 */
	public IProfileChangeRequest getProfileChangeRequest() {
		if (job != null)
			return job.getProfileChangeRequest();
		return null;
	}

	/**
	 * Return a provisioning job that can be used to perform the resolved
	 * operation. The job is created using the default values associated with a
	 * new job. It is up to clients to configure the priority of the job and set
	 * any appropriate properties, such as {@link Job#setUser(boolean)},
	 * {@link Job#setSystem(boolean)}, or
	 * {@link Job#setProperty(QualifiedName, Object)}, before scheduling it.
	 * 
	 * @param monitor
	 *            a progress monitor that should be used to report the job's
	 *            progress in addition to the standard job progress reporting.
	 *            Can be <code>null</code>. If provided, this monitor will be
	 *            called from a background thread.
	 * 
	 * @return a job that can be used to perform the provisioning operation.
	 *         This may be <code>null</code> if the operation has not been
	 *         resolved, or if a plan could not be obtained when attempting to
	 *         resolve. If the job is null and the operation has been resolved,
	 *         then the resolution result will explain the problem.
	 * 
	 * @see #hasResolved()
	 * @see #getResolutionResult()
	 */
	public ProvisioningJob getProvisioningJob(IProgressMonitor monitor) {
		IStatus status = getResolutionResult();
		if (status.getSeverity() != IStatus.CANCEL
				&& status.getSeverity() != IStatus.ERROR) {
			if (job.getProvisioningPlan() != null) {
				ProfileModificationJob pJob = new ProfileModificationJob(
						getProvisioningJobName(), session, profileId,
						job.getProvisioningPlan(),
						job.getActualProvisioningContext());
				pJob.setAdditionalProgressMonitor(monitor);
				return pJob;
			}
		}
		return null;
	}

	/**
	 * Set the provisioning context that should be used to resolve and perform
	 * the provisioning for the operation. This must be set before an attempt is
	 * made to resolve the operation for it to have any effect.
	 * 
	 * @param context
	 *            the provisioning context.
	 */
	public void setProvisioningContext(ProvisioningContext context) {
		this.context = context;
		if (job != null)
			updateJobProvisioningContexts(job, context);
	}

	/**
	 * Get the provisioning context that will be used to resolve and perform the
	 * provisioning for the operation.
	 * 
	 * @return the provisioning context
	 */
	public ProvisioningContext getProvisioningContext() {
		return context;
	}

	public String getProfileId() {
		return profileId;
	}

	/**
	 * Return a boolean indicating whether the operation has been resolved. This
	 * method should be used to determine whether a client can expect to
	 * retrieve a profile change request, provisioning plan, or resolution
	 * result. It is possible that this method return <code>false</code> while
	 * resolution is taking place if it is performed in the background.
	 * 
	 * @return <code>true</code> if the operation has been resolved,
	 *         <code>false</code> if it has not resolved.
	 */
	public boolean hasResolved() {
		return getResolutionResult() != null;
	}

	ProvisioningContext getFirstPassProvisioningContext() {
		return getProvisioningContext();
	}

	IFailedStatusEvaluator getSecondPassEvaluator() {
		return new IFailedStatusEvaluator() {
			public ProvisioningContext getSecondPassProvisioningContext(
					IProvisioningPlan failedPlan) {
				return null;
			}
		};
	}

	protected void updateJobProvisioningContexts(PlannerResolutionJob job,
			ProvisioningContext context) {
		job.setFirstPassProvisioningContext(context);
	}

	protected void computeProfileChangeRequest(MultiStatus status,
			IProgressMonitor monitor) {
		request = ProfileChangeRequest.createByProfileId(
				session.getProvisioningAgent(), profileId);
		IProfile profile;
		int size = 0;
		if (toInstall != null) {
			size += toInstall.size();
		}
		if (toUninstall != null) {
			size += toUninstall.size();
		}
		SubMonitor sub = SubMonitor.convert(monitor,
				Messages.ModifyOperation_ComputeProfileChangeProgress, size);
		profile = ProvUI.getProfileRegistry(
				ProvisioningUI.getDefaultUI().getSession()).getProfile(
				profileId);
		if (toUninstall != null) {
			request.removeAll(toUninstall);
			for (IInstallableUnit iuToUninstall : toUninstall) {
				request.removeInstallableUnitProfileProperty(iuToUninstall,
						IProfile.PROP_PROFILE_ROOT_IU);
				sub.worked(1);
			}
		}
		if (toInstall != null) {
			request.addAll(toInstall);
			for (IInstallableUnit entryToInstall : toInstall) {
				// If the user is installing a patch, we mark it optional. This
				// allows the patched IU to be updated later by removing the
				// patch.
				if (QueryUtil.isPatch(entryToInstall))
					request.setInstallableUnitInclusionRules(
							entryToInstall,
							ProfileInclusionRules
									.createOptionalInclusionRule(entryToInstall));
				else
					request.setInstallableUnitProfileProperty(entryToInstall,
							IProfile.PROP_PROFILE_ROOT_IU,
							Boolean.toString(true));

				sub.worked(1);
			}
		}
		sub.done();
	}
}