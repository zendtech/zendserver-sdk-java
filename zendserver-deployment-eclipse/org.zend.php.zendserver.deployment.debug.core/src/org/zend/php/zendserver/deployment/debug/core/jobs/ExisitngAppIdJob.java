package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class ExisitngAppIdJob extends AbstractLaunchJob {

	public ExisitngAppIdJob(IDeploymentHelper helper, IProject project) {
		super(Messages.ExisitngAppIdJob_JobTitle, helper, project.getLocation()
				.toString());
	}

	public ExisitngAppIdJob(IDeploymentHelper helper, String projectPath) {
		super(Messages.ExisitngAppIdJob_JobTitle, helper, projectPath);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		StatusChangeListener listener = new StatusChangeListener(monitor);
		ZendApplication app = new ZendApplication(new EclipseMappingModelLoader());
		app.addStatusChangeListener(listener);
		ApplicationsList list = app.getStatus(helper.getTargetId());
		List<ApplicationInfo> infos = list.getApplicationsInfo();
		if (infos != null) {
			for (ApplicationInfo info : infos) {
				String appName = info.getAppName();
				if (helper.getAppName().equals(appName)) {
					helper.setAppId(info.getId());
					helper.setInstalledLocation(info
							.getInstalledLocation());
					return new SdkStatus(listener.getStatus());
				}
			}
		}
		return Status.OK_STATUS;
	}

}
