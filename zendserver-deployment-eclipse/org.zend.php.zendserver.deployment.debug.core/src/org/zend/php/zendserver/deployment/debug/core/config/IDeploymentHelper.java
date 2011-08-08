package org.zend.php.zendserver.deployment.debug.core.config;

import java.net.URL;
import java.util.Map;

public interface IDeploymentHelper {

	public static final int DEPLOY = 0;
	public static final int UPDATE = 1;
	public static final int AUTO_DEPLOY = 2;

	URL getBaseURL();

	String getTargetId();

	int getAppId();

	String getProjectName();

	Map<String, String> getUserParams();

	String getAppName();

	String getInstalledLocation();

	boolean isIgnoreFailures();

	boolean isDefaultServer();

	int getOperationType();

	void setBaseURL(String baseURL);

	void setTargetId(String targetId);

	void setAppId(int appId);

	void setProjectName(String projectName);

	void setUserParams(Map<String, String> userParams);

	void setAppName(String appName);

	void setIgnoreFailures(boolean ignoreFailures);

	void setDefaultServer(boolean defaultServer);

	void setOperationType(int type);

	void setInstalledLocation(String location);

}
