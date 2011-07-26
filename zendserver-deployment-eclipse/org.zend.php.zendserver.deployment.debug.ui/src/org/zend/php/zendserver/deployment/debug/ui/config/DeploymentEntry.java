package org.zend.php.zendserver.deployment.debug.ui.config;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.ui.dialogs.DeploymentLaunchDialog;

public class DeploymentEntry {

	private String basePath;
	private String targetId;
	private int appId;
	private String projectName;
	private Map<String, String> userParams;
	private String appName;
	private boolean ignoreFailures;
	private boolean defaultServer;
	private String virtualHost;

	private DeploymentEntry() {
	}

	public DeploymentEntry(String basePath, String targetId, int appId, String projectName,
			Map<String, String> userParams, String appName, boolean ignoreFailures,
			boolean defaultServer, String virtualHost) {
		super();
		this.basePath = basePath;
		this.targetId = targetId;
		this.appId = appId;
		this.projectName = projectName;
		this.userParams = userParams;
		this.appName = appName;
		this.ignoreFailures = ignoreFailures;
		this.defaultServer = defaultServer;
		this.virtualHost = virtualHost;
	}

	public static DeploymentEntry createEntry(DeploymentLaunchDialog dialog, String projectName) {
		DeploymentEntry entry = new DeploymentEntry();
		URL baseURL = dialog.getBaseUrl();
		String targetId = dialog.getTarget().getId();
		entry.basePath = baseURL.getPath();
		entry.projectName = projectName;
		entry.targetId = targetId;
		entry.appId = -1;
		entry.userParams = dialog.getParameters();
		entry.appName = dialog.getUserAppName();
		entry.ignoreFailures = dialog.isIgnoreFailures();
		entry.defaultServer = dialog.isDefaultServer();
		entry.virtualHost = baseURL.getHost();
		return entry;
	}

	@SuppressWarnings("unchecked")
	public static DeploymentEntry createEntry(ILaunchConfiguration config) {
		DeploymentEntry entry = new DeploymentEntry();
		try {
			entry.basePath = config.getAttribute(DeploymentAttributes.BASE_PATH.getName(), "");
			entry.targetId = config.getAttribute(DeploymentAttributes.TARGET_ID.getName(), "");
			entry.appId = config.getAttribute(DeploymentAttributes.APP_ID.getName(), -1);
			entry.projectName = config
					.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(), "");
			entry.userParams = config.getAttribute(DeploymentAttributes.PARAMETERS.getName(),
					Collections.emptyMap());
			entry.appName = config
					.getAttribute(DeploymentAttributes.APPLICATION_NAME.getName(), "");
			entry.ignoreFailures = config.getAttribute(
					DeploymentAttributes.IGNORE_FAILURES.getName(), true);
			entry.defaultServer = config.getAttribute(
					DeploymentAttributes.DEFAULT_SERVER.getName(), true);
			entry.virtualHost = config
					.getAttribute(DeploymentAttributes.VIRTUAL_HOST.getName(), "");
		} catch (CoreException e) {
			return null;
		}
		return entry;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getTargetId() {
		return targetId;
	}

	public int getAppId() {
		return appId;
	}

	public String getProjectName() {
		return projectName;
	}

	public Map<String, String> getUserParams() {
		return userParams;
	}

	public String getAppName() {
		return appName;
	}

	public boolean isIgnoreFailures() {
		return ignoreFailures;
	}

	public boolean isDefaultServer() {
		return defaultServer;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

}
