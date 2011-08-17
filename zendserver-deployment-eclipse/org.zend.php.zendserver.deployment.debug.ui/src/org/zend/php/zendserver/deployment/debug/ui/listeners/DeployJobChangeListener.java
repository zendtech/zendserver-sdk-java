package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeployJobChangeListener extends JobChangeAdapter {

	private ILaunchConfiguration config;
	private boolean cancelled;

	public DeployJobChangeListener(ILaunchConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void done(IJobChangeEvent event) {
		DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) event.getJob();
		int status = event.getResult().getSeverity();
		switch (status) {
		case IStatus.OK:
			if (deploymentJob.getResponseCode() == null) {
				IProject project;
				try {
					project = LaunchUtils.getProjectFromFilename(config);
					updateLaunchConfiguration(deploymentJob, config, project);
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
			break;
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

	private void updateLaunchConfiguration(DeploymentLaunchJob job,
			final ILaunchConfiguration config, final IProject project) throws CoreException {
		ResponseCode code = job.getResponseCode();
		if (code != null) {
			return;
		}
		ILaunchConfigurationWorkingCopy wc = null;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy) config;
		} else {
			wc = config.getWorkingCopy();
		}
		IDeploymentHelper helper = job.getHelper();
		LaunchUtils.updateLaunchConfiguration(project, helper, wc);
		if (helper.getOperationType() == IDeploymentHelper.DEPLOY) {
			wc.setAttribute(DeploymentAttributes.OPERATION_TYPE.getName(), IDeploymentHelper.UPDATE);
		}
		wc.doSave();
	}
}