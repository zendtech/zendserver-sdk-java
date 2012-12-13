package org.zend.php.zendserver.deployment.debug.core.jobs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.Messages;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

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
		ZendApplication app = new ZendApplication(
				new EclipseMappingModelLoader());
		app.addStatusChangeListener(listener);
		ApplicationsList list = app.getStatus(helper.getTargetId());
		List<ApplicationInfo> infos = list.getApplicationsInfo();
		if (infos != null) {
			URL url = helper.getBaseURL();
			if (helper.isDefaultServer()) {
				try {
					String defaultHost = IDeploymentHelper.DEFAULT_SERVER;
					if (checkTargetVersion(helper.getTargetId())) {
						defaultHost = helper.getBaseURL().getHost();
					}
					url = new URL(url.getProtocol(), defaultHost, url.getFile());
				} catch (MalformedURLException e) {
					// ignore
				}
			}

			for (ApplicationInfo info : infos) {
				URL baseUrl = null;
				try {
					baseUrl = new URL(info.getBaseUrl());
				} catch (MalformedURLException e) {
					// / ignore
				}
				if (compareURLs(url, baseUrl)) {
					helper.setAppId(info.getId());
					helper.setInstalledLocation(info.getInstalledLocation());
					break;
				}
			}
		}
		return new SdkStatus(listener.getStatus());
	}

	private boolean checkTargetVersion(String targetId) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		return TargetsManager.isPhpcloud(target)
				&& ZendServerVersion
						.byName(target.getProperty(IZendTarget.SERVER_VERSION))
						.getName().startsWith("6"); //$NON-NLS-1$
	}

	private boolean compareURLs(URL current, URL url) {
		if (current == null || url == null) {
			return false;
		}
		if (current.getProtocol().equals(url.getProtocol())
				&& current.getHost().equals(url.getHost())
				&& current.getFile().equals(url.getFile())) {
			return true;
		}
		return false;
	}

}
