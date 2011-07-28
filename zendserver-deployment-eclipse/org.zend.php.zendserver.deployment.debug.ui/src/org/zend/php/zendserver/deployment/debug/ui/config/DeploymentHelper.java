package org.zend.php.zendserver.deployment.debug.ui.config;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.dialogs.DeploymentLaunchDialog;

public class DeploymentHelper implements IDeploymentHelper {

	private static final String EMPTY_STRING = "";

	private String basePath;
	private String targetId;
	private int appId;
	private String projectName;
	private Map<String, String> userParams;
	private String appName;
	private boolean ignoreFailures;
	private boolean defaultServer;
	private String virtualHost;

	private DeploymentHelper() {
	}

	// TODO consider to remove this constructor
	public DeploymentHelper(String basePath, String targetId, int appId, String projectName,
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

	public static DeploymentHelper create(DeploymentLaunchDialog dialog, String projectName) {
		DeploymentHelper entry = new DeploymentHelper();
		URL baseURL = dialog.getBaseUrl();
		String targetId = dialog.getTarget().getId();
		entry.setBasePath(baseURL.getPath());
		entry.setProjectName(projectName);
		entry.setTargetId(targetId);
		entry.setAppId(-1);
		entry.setUserParams(dialog.getParameters());
		entry.setAppName(dialog.getUserAppName());
		entry.setIgnoreFailures(dialog.isIgnoreFailures());
		entry.setDefaultServer(dialog.isDefaultServer());
		entry.setVirtualHost(baseURL.getHost());
		return entry;
	}

	@SuppressWarnings("unchecked")
	public static DeploymentHelper create(ILaunchConfiguration config) {
		DeploymentHelper entry = new DeploymentHelper();
		try {
			entry.setBasePath(config.getAttribute(DeploymentAttributes.BASE_PATH.getName(),
					EMPTY_STRING));
			entry.setTargetId(config.getAttribute(DeploymentAttributes.TARGET_ID.getName(),
					EMPTY_STRING));
			entry.setAppId(config.getAttribute(DeploymentAttributes.APP_ID.getName(), -1));
			entry.setProjectName(config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(),
					EMPTY_STRING));
			entry.setUserParams(config.getAttribute(DeploymentAttributes.PARAMETERS.getName(),
					Collections.emptyMap()));
			entry.setAppName(config.getAttribute(DeploymentAttributes.APPLICATION_NAME.getName(),
					EMPTY_STRING));
			entry.setIgnoreFailures(config.getAttribute(
					DeploymentAttributes.IGNORE_FAILURES.getName(), true));
			entry.setDefaultServer(config.getAttribute(
					DeploymentAttributes.DEFAULT_SERVER.getName(), true));
			entry.setVirtualHost(config.getAttribute(DeploymentAttributes.VIRTUAL_HOST.getName(),
					EMPTY_STRING));
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

	protected void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	protected void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	protected void setAppId(int appId) {
		this.appId = appId;
	}

	protected void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	protected void setUserParams(Map<String, String> userParams) {
		this.userParams = userParams;
	}

	protected void setAppName(String appName) {
		this.appName = appName;
	}

	protected void setIgnoreFailures(boolean ignoreFailures) {
		this.ignoreFailures = ignoreFailures;
	}

	protected void setDefaultServer(boolean defaultServer) {
		this.defaultServer = defaultServer;
	}

	protected void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

}
