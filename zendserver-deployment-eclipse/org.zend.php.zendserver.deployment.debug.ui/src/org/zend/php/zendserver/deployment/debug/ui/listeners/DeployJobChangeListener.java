package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.sdklib.manager.TargetsManager;

public class DeployJobChangeListener extends JobChangeAdapter {

	private ILaunchConfiguration config;
	private boolean cancelled;

	public DeployJobChangeListener(ILaunchConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void done(IJobChangeEvent event) {
		AbstractLaunchJob launchJob = (AbstractLaunchJob) event.getJob();
		int status = event.getResult().getSeverity();
		switch (status) {
		case IStatus.OK:
			if (launchJob instanceof DeploymentLaunchJob) {
				DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) launchJob;
				if (deploymentJob.getResponseCode() != null) {
					return;
				}
			}
			IProject project;
			try {
				project = LaunchUtils.getProjectFromFilename(config);
				updateLaunchConfiguration(launchJob, project);
			} catch (CoreException e) {
				Activator.log(e);
			}
			break;
		case IStatus.ERROR:
		case IStatus.CANCEL:
			cancelled = true;
			break;
		default:
			break;
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	private void updateLaunchConfiguration(AbstractLaunchJob job, final IProject project)
			throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy) config;
		} else {
			wc = config.getWorkingCopy();
		}
		IDeploymentHelper helper = job.getHelper();
		LaunchUtils.updateLaunchConfiguration(project, helper, wc);
		if (helper.getOperationType() == IDeploymentHelper.DEPLOY) {
			String host = helper.getTargetHost();
			if (LaunchUtils.isAutoDeployAvailable() && TargetsManager.isLocalhost(host)) {
				wc.setAttribute(DeploymentAttributes.OPERATION_TYPE.getName(), IDeploymentHelper.NO_ACTION);
			} else {
				wc.setAttribute(DeploymentAttributes.OPERATION_TYPE.getName(), IDeploymentHelper.UPDATE);
			}
		}
		wc.doSave();
	}
}