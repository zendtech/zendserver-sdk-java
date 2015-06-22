package org.zend.php.zendserver.deployment.debug.ui.config;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.ParameterType;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.ExisitngAppIdJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeployJobChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeploymentHandler {

	private AbstractLaunchJob job;

	private boolean dialogResult;
	private boolean cancelled;

	private DeployJobChangeListener listener;

	public DeploymentHandler() {
		super();
	}

	public int executeDeployment(ILaunchConfiguration config, String executionMode) {
		try {
			boolean isRunAs = LaunchUtils.updateConfigForRunAs(config);
			if (isRunAs) {
				return IStatus.OK;
			}
		} catch (CoreException e) {
			Activator.log(e);
			return IStatus.ERROR;
		}
		
		try {
			if (LaunchUtils.getConfigurationType() != config.getType())
				return IStatus.OK;
			
			IDeploymentHelper helper = DeploymentHelper.create(config);
			if (!helper.isEnabled()) {
				return IStatus.OK;
			}
			
			listener = new DeployJobChangeListener(config);
			final IProject project = LaunchUtils.getProjectFromFilename(config);

			switch (helper.getOperationType()) {
			case IDeploymentHelper.DEPLOY:
				if (helper.getTargetId().isEmpty()) {
					IDeploymentHelper defaultHelper = LaunchUtils.createDefaultHelper(project);
					if (defaultHelper != null) {
						try {
							helper = defaultHelper;
							LaunchUtils.updateLaunchConfiguration(project, defaultHelper, config.getWorkingCopy());
						} catch (CoreException e) {
							Activator.log(e);
						}
					}
				}
				if (helper.getTargetId().isEmpty() || hasEmptyParameters(project, helper)) {
					setDefaultTarget(helper, project);
					if (helper.getTargetId() != null || !helper.getTargetId().isEmpty()) {
						helper.setProjectName(project.getName());
					}
					helper = doOpenDeploymentWizard(helper, project, executionMode);
				}
				return performJob(helper, project);
			case IDeploymentHelper.UPDATE:
				if (hasEmptyParameters(project, helper)) {
					setDefaultTarget(helper, project);
					if (helper.getTargetId() != null || !helper.getTargetId().isEmpty()) {
						helper.setProjectName(project.getName());
					}
					helper = doOpenDeploymentWizard(helper, project, executionMode);
				}
				return performJob(helper, project);
			case IDeploymentHelper.AUTO_DEPLOY:
				return performJob(helper, project);
			case IDeploymentHelper.NO_ACTION:
				updateLaunchConfiguration(helper, config, project);
				return IStatus.OK;
			default:
				return IStatus.CANCEL;
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	public int openNoConfigDeploymentWizard(IDeploymentHelper helper,
			IProject project) {
		helper = doOpenDeploymentWizard(helper, project);
		if (helper == null) {
			return IStatus.CANCEL;
		}
		return performJob(helper, project);
	}

	/**
	 * Perform a deployment job base on provided {@link IDeploymentHelper} and a
	 * project.
	 * 
	 * @param helper
	 *            {@link IDeploymentHelper} instance with deployment parameters
	 * @param project
	 *            {@link IProject} instance
	 * @return {@link IStatus#OK} if action was performed successfully;
	 *         otherwise return {@link IStatus#CANCEL}
	 */
	public int performJob(IDeploymentHelper helper, IProject project) {
		switch (helper.getOperationType()) {
		case IDeploymentHelper.DEPLOY:
			job = new DeployLaunchJob(helper, project);
			break;
		case IDeploymentHelper.UPDATE:
			job = new UpdateLaunchJob(helper, project);
			break;
		case IDeploymentHelper.AUTO_DEPLOY:
			job = LaunchUtils.getAutoDeployJob();
			if (job == null) {
				return IStatus.CANCEL;
			}
			job.setHelper(helper);
			job.setProjectPath(project);
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().getSeverity() == IStatus.CANCEL) {
						cancelled = true;
					}
				}
			});
			break;
		}
		
		if (job == null)
			return IStatus.OK;
		
		if (listener != null)
			job.addJobChangeListener(listener);
		job.setUser(true);
		job.schedule();
		try {
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	private void setDefaultTarget(IDeploymentHelper helper,
			final IProject project) {
		String targetId = helper.getTargetId();
		if (targetId == null || targetId.isEmpty()) {
			IZendTarget defaultTarget = LaunchUtils.getDefaultTarget(project);
			if (defaultTarget != null) {
				helper.setTargetId(defaultTarget.getId());
				helper.setTargetHost(defaultTarget.getHost().toString());
			}
		}
	}

	private boolean hasEmptyParameters(IProject project, IDeploymentHelper helper) {
		IResource descriptor = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		List<IParameter> definedParams = model.getDescriptorModel()
				.getParameters();
		int size = 0;
		for (IParameter param : definedParams) {
			ParameterType type = ParameterType.byName(param.getType());
			if (type != null) {
				size++;
			}
		}
		if (definedParams == null || size == 0) {
			return false;
		}
		Map<String, String> params = helper.getUserParams();
		if (params != null && params.size() > 0) {
			for (IParameter parameter : definedParams) {
				String value = (String) params.get(parameter.getId());
				if (parameter.isRequired()
						&& (value == null || value.isEmpty())) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	private boolean isCancelled() {
		if (listener != null) {
			return listener.isCancelled() || cancelled;
		}
		return cancelled;
	}

	private int verifyJobResult(final IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		if (isCancelled()) {
			return IStatus.CANCEL;
		}
		if (job instanceof DeploymentLaunchJob) {
			DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
			ResponseCode code = deploymentJob.getResponseCode();
			if (code == null) {
				if (helper.getInstalledLocation() == null
						|| helper.getInstalledLocation().trim().isEmpty()) {
					return IStatus.CANCEL;
				}
				if (helper.isDevelopmentModeEnabled()) {
					MonitorManager.addFilter(helper.getTargetId(), helper
							.getBaseURL().toString());
					if (LaunchUtils.isAutoDeployAvailable()
							&& (helper.getOperationType() == IDeploymentHelper.DEPLOY || helper
									.getOperationType() == IDeploymentHelper.UPDATE)) {
						job = getAutoDeployJob(helper, project);
					}
				}
				LaunchUtils.updatePreferences(project, helper.getTargetId(),
						helper.getBaseURL().toString());
			} else if (code == ResponseCode.INVALID_PARAMETER
					|| (code == ResponseCode.APPLICATION_CONFLICT && helper
							.getOperationType() != IDeploymentHelper.UPDATE)) {
				// handle correctly invalid response (INVALID_PARAMENTER) and
				// the correct one (APPLICATION_CONFLICT) but do not handle
				// conflict if it exists for an update operation because it
				// means that application is different than the one on the
				// server
				return handleConflict(helper, project,
						deploymentJob.getResponseCode());
			}
		}
		return IStatus.OK;
	}

	private AbstractLaunchJob getAutoDeployJob(IDeploymentHelper helper,
			IProject project) throws InterruptedException {
		AbstractLaunchJob job = LaunchUtils.getAutoDeployJob();
		if (job == null) {
			// if auto deploy is not available then just leave
			// configuration without changes
			return null;
		}
		helper.setOperationType(IDeploymentHelper.NO_ACTION);
		job.setHelper(helper);
		job.setProjectPath(project);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().getSeverity() == IStatus.CANCEL) {
					cancelled = true;
				}
			}
		});
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setUser(true);
		job.schedule();
		job.join();
		return job;
	}

	private int handleConflict(IDeploymentHelper helper, IProject project,
			ResponseCode code) throws InterruptedException {
		dialogResult = false;
		if (helper.isWarnUpdate()) {
			IStatus result = job.getResult();
			if (result.getSeverity() == IStatus.INFO) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						MessageDialog dialog = getUpdateExistingApplicationDialog(Messages.DeploymentHandler_ApplicationNameErrorMessage);
						if (dialog.open() == 0) {
							dialogResult = true;
						}
					}
				});
			}
		} else {
			dialogResult = true;
		}
		if (!dialogResult) {
			helper = doOpenDeploymentWizard(helper, project);
			if (helper == null) {
				return IStatus.CANCEL;
			}
			return performJob(helper, project);
		} else {
			dialogResult = false;
			helper.setOperationType(DeploymentHelper.UPDATE);
			job = new ExisitngAppIdJob(helper, project);
			job.setUser(true);
			job.schedule();
			job.join();
			if (job.getHelper().getAppId() != -1) {
				job = new UpdateLaunchJob(job.getHelper(), project);
				if (listener != null) {
					job.addJobChangeListener(listener);
				}
				job.setUser(true);
				job.schedule();
				job.join();
				return verifyJobResult(job.getHelper(), project);
			} else {
				return IStatus.CANCEL;
			}
		}
	}

	private IDeploymentHelper doOpenDeploymentWizard(final IDeploymentHelper helper, final IProject project,
			String launchMode) {
		Mode wizardMode = Mode.DEBUG;
		if (ILaunchManager.RUN_MODE.equals(launchMode))
			wizardMode = Mode.RUN;

		return doOpenDeploymentWizard(helper, project, wizardMode);
	}

	private IDeploymentHelper doOpenDeploymentWizard(final IDeploymentHelper helper, final IProject project) {
		return doOpenDeploymentWizard(helper, project, Mode.DEPLOY);
	}

	private IDeploymentHelper doOpenDeploymentWizard(final IDeploymentHelper helper, final IProject project,
			final Mode wizardMode) {

		final DeploymentWizard wizard = new DeploymentWizard(project, helper, wizardMode);
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialogResult = (dialog.open() == Window.OK);
			}
		});
		IDeploymentHelper helper2 = null;
		if (dialogResult)
			helper2 = wizard.getHelper();
		return helper2;
	}

	private void updateLaunchConfiguration(IDeploymentHelper helper,
			final ILaunchConfiguration config, final IProject project)
			throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy) config;
		} else {
			wc = config.getWorkingCopy();
		}
		LaunchUtils.updateLaunchConfiguration(project, helper, wc);
		wc.doSave();
	}

	private MessageDialog getUpdateExistingApplicationDialog(String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		return new MessageDialog(shell,
				Messages.updateExistingApplicationDialog_Title, null, message,
				MessageDialog.QUESTION, new String[] {
						Messages.updateExistingApplicationDialog_yesButton,
						Messages.updateExistingApplicationDialog_noButton }, 1);
	}

}
