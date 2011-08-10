package org.zend.php.zendserver.deployment.debug.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;

public class DeploymentHelper implements IDeploymentHelper {

	private static final String EMPTY_STRING = "";

	private URL baseURL;
	private String targetId;
	private String targetHost;
	private int appId;
	private String projectName;
	private Map<String, String> userParams;
	private String appName;
	private boolean ignoreFailures;
	private boolean defaultServer;
	private String virtualHost;
	private int operationType;
	private String installedLocation;

	public DeploymentHelper() {
		this.baseURL = null;
		this.targetId = EMPTY_STRING;
		this.appId = -1;
		this.projectName = EMPTY_STRING;
		this.userParams = new HashMap<String, String>();
		this.appName = EMPTY_STRING;
		this.ignoreFailures = false;
		this.defaultServer = false;
		this.virtualHost = EMPTY_STRING;
		this.operationType = IDeploymentHelper.DEPLOY;
	}

	@SuppressWarnings("unchecked")
	public static DeploymentHelper create(ILaunchConfiguration config) {
		DeploymentHelper helper = new DeploymentHelper();
		try {
			helper.setBaseURL(config.getAttribute(DeploymentAttributes.BASE_URL.getName(),
					EMPTY_STRING));
			helper.setTargetId(config.getAttribute(DeploymentAttributes.TARGET_ID.getName(),
					EMPTY_STRING));
			helper.setTargetHost(config.getAttribute(DeploymentAttributes.TARGET_HOST.getName(),
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
			helper.setOperationType(config.getAttribute(
					DeploymentAttributes.OPERATION_TYPE.getName(), IDeploymentHelper.DEPLOY));
			helper.setInstalledLocation(config.getAttribute(
					DeploymentAttributes.INSTALLED_LOCATION.getName(), EMPTY_STRING));
		} catch (CoreException e) {
			return null;
		}
		return helper;
	}

	public URL getBaseURL() {
		return baseURL;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getTargetHost() {
		return targetHost;
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

	public int getOperationType() {
		return operationType;
	}

	public String getInstalledLocation() {
		return installedLocation;
	}

	public void setBaseURL(String baseURL) {
		try {
			this.baseURL = new URL(baseURL);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid base URL: " + baseURL);
		}
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
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

	public void setOperationType(int type) {
		this.operationType = type;
	}

	public void setInstalledLocation(String location) {
		this.installedLocation = location;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDeploymentHelper) {
			IDeploymentHelper helper = (IDeploymentHelper) obj;
			if (getAppId() != helper.getAppId()) {
				return false;
			}
			if (!getAppName().equals(helper.getAppName())) {
				return false;
			}
			if (!getBaseURL().toString().equals(helper.getBaseURL().toString())) {
				return false;
			}
			if (!getInstalledLocation().equals(helper.getInstalledLocation())) {
				return false;
			}
			if (getOperationType() != helper.getOperationType()) {
				return false;
			}
			if (!getProjectName().equals(helper.getProjectName())) {
				return false;
			}
			if (!getTargetId().equals(helper.getTargetId())) {
				return false;
			}
			if (!getVirtualHost().equals(helper.getVirtualHost())) {
				return false;
			}
			return true;
		}
		return false;
	}

}
