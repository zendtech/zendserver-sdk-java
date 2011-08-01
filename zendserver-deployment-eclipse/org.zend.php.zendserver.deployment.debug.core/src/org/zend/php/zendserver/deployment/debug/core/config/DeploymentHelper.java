package org.zend.php.zendserver.deployment.debug.core.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;

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

	public DeploymentHelper() {
		this.basePath = EMPTY_STRING;
		this.targetId = EMPTY_STRING;
		this.appId = -1;
		this.projectName = EMPTY_STRING;
		this.userParams = new HashMap<String, String>();
		this.appName = EMPTY_STRING;
		this.ignoreFailures = false;
		this.defaultServer = false;
		this.virtualHost = EMPTY_STRING;
	}

	@SuppressWarnings("unchecked")
	public static DeploymentHelper create(ILaunchConfiguration config) {
		DeploymentHelper helper = new DeploymentHelper();
		try {
			helper.setBasePath(config.getAttribute(DeploymentAttributes.BASE_PATH.getName(),
					EMPTY_STRING));
			helper.setTargetId(config.getAttribute(DeploymentAttributes.TARGET_ID.getName(),
					EMPTY_STRING));
			helper.setAppId(config.getAttribute(DeploymentAttributes.APP_ID.getName(), -1));
			helper.setProjectName(config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(),
					EMPTY_STRING));
			helper.setUserParams(config.getAttribute(DeploymentAttributes.PARAMETERS.getName(),
					Collections.emptyMap()));
			helper.setAppName(config.getAttribute(DeploymentAttributes.APPLICATION_NAME.getName(),
					EMPTY_STRING));
			helper.setIgnoreFailures(config.getAttribute(
					DeploymentAttributes.IGNORE_FAILURES.getName(), true));
			helper.setDefaultServer(config.getAttribute(
					DeploymentAttributes.DEFAULT_SERVER.getName(), true));
			helper.setVirtualHost(config.getAttribute(DeploymentAttributes.VIRTUAL_HOST.getName(),
					EMPTY_STRING));
		} catch (CoreException e) {
			return null;
		}
		return helper;
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

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setUserParams(Map<String, String> userParams) {
		this.userParams = userParams;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setIgnoreFailures(boolean ignoreFailures) {
		this.ignoreFailures = ignoreFailures;
	}

	public void setDefaultServer(boolean defaultServer) {
		this.defaultServer = defaultServer;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

}
