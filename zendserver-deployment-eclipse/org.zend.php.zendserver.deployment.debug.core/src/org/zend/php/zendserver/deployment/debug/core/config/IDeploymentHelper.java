package org.zend.php.zendserver.deployment.debug.core.config;

import java.util.Map;

public interface IDeploymentHelper {

	String getBasePath();

	String getTargetId();

	int getAppId();

	String getProjectName();

	Map<String, String> getUserParams();

	String getAppName();

	boolean isIgnoreFailures();

	boolean isDefaultServer();

	String getVirtualHost();

}
