package org.zend.php.zendserver.deployment.debug.core.config;

import java.net.URL;
import java.util.Map;

public interface IDeploymentHelper {

	URL getBaseURL();

	String getTargetId();

	int getAppId();

	String getProjectName();

	Map<String, String> getUserParams();

	String getAppName();

	boolean isIgnoreFailures();

	boolean isDefaultServer();

	void setBaseURL(String baseURL);

	void setTargetId(String targetId);

	void setAppId(int appId);

	void setProjectName(String projectName);

	void setUserParams(Map<String, String> userParams);

	void setAppName(String appName);

	void setIgnoreFailures(boolean ignoreFailures);

	void setDefaultServer(boolean defaultServer);

}
