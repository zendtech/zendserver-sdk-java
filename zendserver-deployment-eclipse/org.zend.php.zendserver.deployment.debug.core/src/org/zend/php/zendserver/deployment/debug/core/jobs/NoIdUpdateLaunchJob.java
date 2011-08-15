package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class NoIdUpdateLaunchJob extends DeploymentLaunchJob {

	public NoIdUpdateLaunchJob(IDeploymentHelper helper, IProject project) {
		super(Messages.updateJob_Title, helper, project);
	}

	@Override
	protected ApplicationInfo performOperation(ZendApplication app, String projectPath) {
		ApplicationsList list = app.getStatus(helper.getTargetId());
		List<ApplicationInfo> infos = list.getApplicationsInfo();
		String appId = null;
		if (infos != null) {
			URL url = helper.getBaseURL();
			if (helper.isDefaultServer()) {
				try {
					url = new URL(url.getProtocol(), IDeploymentHelper.DEFAULT_SERVER,
							url.getFile());
				} catch (MalformedURLException e) {
					// ignore
				}
			}
			String urlString = url.toString();
			for (ApplicationInfo info : infos) {
				if (info.getBaseUrl().equals(urlString)) {
					appId = String.valueOf(info.getId());
					break;
				}
			}
		}
		if (appId != null) {
			return app.update(project.getLocation().toString(), helper.getTargetId(), appId,
					helper.getUserParams(), helper.isIgnoreFailures());
		}
		// should not happen because the same base URL is already used
		return null;
	}

}
