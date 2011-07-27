package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;

public abstract class AbstractLaunchJob extends Job {

	protected IDeploymentHelper entry;
	protected IProject project;
	private ILaunchConfiguration config;

	protected AbstractLaunchJob(String name, IDeploymentHelper entry, IProject project) {
		super(name);
		this.entry = entry;
		this.project = project;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		StatusChangeListener listener = new StatusChangeListener(monitor);
		ZendApplication app = new ZendApplication(new EclipseMappingModelLoader());
		app.addStatusChangeListener(listener);
		ApplicationInfo info = performOperation(app, project.getLocation().toString());
		if (monitor.isCanceled()) {
			return Status.OK_STATUS;
		}
		if (info != null && info.getStatus() == ApplicationStatus.STAGING) {
			ILaunchConfiguration newConfig = null;
			try {
				newConfig = createLaunchConfiguration(info.getId());
			} catch (CoreException e) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
			}
			if (newConfig != null) {
				config = newConfig;
			}
			return monitorApplicationStatus(listener, entry.getTargetId(), info.getId(),
					app, monitor);
		}
		return new SdkStatus(listener.getStatus());
	}

	public ILaunchConfiguration getConfig() {
		return config;
	}

	protected abstract ILaunchConfiguration createLaunchConfiguration(int appId)
			throws CoreException;

	protected abstract ApplicationInfo performOperation(ZendApplication app, String projectPath);

	private IStatus monitorApplicationStatus(StatusChangeListener listener, String targetId,
			int id, ZendApplication application, IProgressMonitor monitor) {
		monitor.beginTask(Messages.statusJob_Title, IProgressMonitor.UNKNOWN);
		ApplicationStatus result = null;
		while (result != ApplicationStatus.DEPLOYED) {
			if (monitor.isCanceled()) {
				return Status.OK_STATUS;
			}
			ApplicationsList info = application.getStatus(targetId, String.valueOf(id));
			if (info != null && info.getApplicationsInfo() != null) {
				result = info.getApplicationsInfo().get(0).getStatus();
				if (isErrorStatus(result)) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Error on the Zend Server during application deployment: "
									+ result.getName()
									+ "\nTo get more details, see Zend Server log file.");
				}
				monitor.subTask("Current status is: " + result.getName());
			}
		}
		monitor.done();
		return new SdkStatus(listener.getStatus());
	}

	private boolean isErrorStatus(ApplicationStatus result) {
		switch (result) {
		case ACTIVATE_ERROR:
		case DEACTIVATE_ERROR:
		case STAGE_ERROR:
		case UNSTAGE_ERROR:
		case UPLOAD_ERROR:
			return true;
		default:
			return false;
		}
	}

}
