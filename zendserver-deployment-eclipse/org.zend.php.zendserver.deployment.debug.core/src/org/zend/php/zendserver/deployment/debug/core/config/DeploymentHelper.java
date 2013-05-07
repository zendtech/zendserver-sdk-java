package org.zend.php.zendserver.deployment.debug.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;

public class DeploymentHelper implements IDeploymentHelper {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

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
	private boolean enabled;

	private boolean developmentMode;

	private boolean warnUpdate;

	private Map<String, String> extraAttributes;

	public DeploymentHelper() {
		this.baseURL = null;
		this.targetId = EMPTY_STRING;
		this.targetHost = EMPTY_STRING;
		this.appId = -1;
		this.projectName = EMPTY_STRING;
		this.userParams = new HashMap<String, String>();
		this.appName = EMPTY_STRING;
		this.installedLocation = EMPTY_STRING;
		this.ignoreFailures = false;
		this.defaultServer = false;
		this.virtualHost = EMPTY_STRING;
		this.operationType = IDeploymentHelper.DEPLOY;
		this.enabled = true;
		this.developmentMode = true;
		this.warnUpdate = false;
	}

	@SuppressWarnings("unchecked")
	public static DeploymentHelper create(ILaunchConfiguration config) {
		DeploymentHelper helper = new DeploymentHelper();
		try {
			String baseURL = config.getAttribute(
					DeploymentAttributes.BASE_URL.getName(), EMPTY_STRING);
			if (!baseURL.isEmpty()) {
				helper.setBaseURL(baseURL);
			}
			helper.setTargetId(config.getAttribute(
					DeploymentAttributes.TARGET_ID.getName(), EMPTY_STRING));
			helper.setTargetHost(config.getAttribute(
					DeploymentAttributes.TARGET_HOST.getName(), EMPTY_STRING));
			helper.setAppId(config.getAttribute(
					DeploymentAttributes.APP_ID.getName(), -1));
			helper.setProjectName(config.getAttribute(
					DeploymentAttributes.PROJECT_NAME.getName(), EMPTY_STRING));
			helper.setUserParams(config.getAttribute(
					DeploymentAttributes.PARAMETERS.getName(),
					Collections.emptyMap()));
			helper.setAppName(config.getAttribute(
					DeploymentAttributes.APPLICATION_NAME.getName(),
					EMPTY_STRING));
			helper.setIgnoreFailures(config.getAttribute(
					DeploymentAttributes.IGNORE_FAILURES.getName(), false));
			helper.setDefaultServer(config.getAttribute(
					DeploymentAttributes.DEFAULT_SERVER.getName(), true));
			helper.setOperationType(config.getAttribute(
					DeploymentAttributes.OPERATION_TYPE.getName(),
					IDeploymentHelper.DEPLOY));
			helper.setInstalledLocation(config.getAttribute(
					DeploymentAttributes.INSTALLED_LOCATION.getName(),
					EMPTY_STRING));
			helper.setEnabled(config.getAttribute(
					DeploymentAttributes.ENABLED.getName(), true));
			helper.setDevelopmentMode(config.getAttribute(
					DeploymentAttributes.DEVELOPMENT_MODE.getName(), true));
			helper.setWarnUpdate(config.getAttribute(
					DeploymentAttributes.WARN_UPDATE.getName(), false));
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

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isDevelopmentModeEnabled() {
		return developmentMode;
	}

	public boolean isWarnUpdate() {
		return warnUpdate;
	}
	
	public Map<String, String> getExtraAttributes() {
		return extraAttributes;
	}

	public void setBaseURL(String baseURL) {
		try {
			this.baseURL = new URL(baseURL);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Invalid base URL: " + baseURL); //$NON-NLS-1$
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

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDevelopmentMode(boolean enabled) {
		this.developmentMode = enabled;
	}

	public void setWarnUpdate(boolean enabled) {
		this.warnUpdate = enabled;
	}

	public void setExtraAtttributes(Map<String, String> extraAttributes) {
		this.extraAttributes = extraAttributes;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDeploymentHelper) {
			IDeploymentHelper h = (IDeploymentHelper) obj;
			if (getAppId() != h.getAppId()) {
				return false;
			}
			if (!getAppName().equals(h.getAppName())) {
				return false;
			}
			URL baseURL = h.getBaseURL();
			if (baseURL != null && getBaseURL() != null) {
				if (!getBaseURL().toString().equals(baseURL.toString())) {
					return false;
				}
			}
			if (!getInstalledLocation().equals(h.getInstalledLocation())) {
				return false;
			}
			if (getOperationType() != h.getOperationType()) {
				return false;
			}
			if (!getProjectName().equals(h.getProjectName())) {
				return false;
			}
			if (!getTargetHost().equals(h.getTargetHost())) {
				return false;
			}
			if (!getTargetId().equals(h.getTargetId())) {
				return false;
			}
			if (!getVirtualHost().equals(h.getVirtualHost())) {
				return false;
			}
			if (!compareParams(getUserParams(), h.getUserParams())) {
				return false;
			}
			if (isIgnoreFailures() != h.isIgnoreFailures()) {
				return false;
			}
			if (isDefaultServer() != h.isDefaultServer()) {
				return false;
			}
			if (isEnabled() != h.isEnabled()) {
				return false;
			}
			if (isDevelopmentModeEnabled() != h.isDevelopmentModeEnabled()) {
				return false;
			}
			if (isWarnUpdate() != h.isWarnUpdate()) {
				return false;
			}
		}
		return true;
	}

	private boolean compareParams(Map<String, String> current,
			Map<String, String> other) {
		Set<Entry<String, String>> entries = current.entrySet();
		for (Entry<String, String> entry : entries) {
			String value = other.get(entry.getKey());
			if (value == null || !entry.getValue().equals(value)) {
				return false;
			}
		}
		return true;
	}

}
