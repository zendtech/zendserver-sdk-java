package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
	protected ApplicationInfo performOperation(ZendApplication app,
			String projectPath) {
		ApplicationsList list = app.getStatus(helper.getTargetId());
		List<ApplicationInfo> infos = list.getApplicationsInfo();
		String appId = null;
		if (infos != null) {
			URL url = helper.getBaseURL();
			if (helper.isDefaultServer()) {
				try {
					url = new URL(url.getProtocol(),
							IDeploymentHelper.DEFAULT_SERVER, url.getFile());
				} catch (MalformedURLException e) {
					// ignore
				}
			}
			IPath urlPath = new Path(url.toString());
			final int segmentCount = urlPath.segmentCount();

			for (ApplicationInfo info : infos) {
				final IPath baseUrl = new Path(info.getBaseUrl());
				// ignore the device (protocol) when comparing urls, this is
				// useful because servers can manage their
				// own protocols in different ways
				if (baseUrl.matchingFirstSegments(urlPath) == segmentCount) {
					appId = String.valueOf(info.getId());
					break;
				}
			}
		}
		if (appId != null) {
			return app.update(project.getLocation().toString(),
					helper.getTargetId(), appId, helper.getUserParams(),
					helper.isIgnoreFailures());
		}
		// should not happen because the same base URL is already used
		return null;
	}

}
