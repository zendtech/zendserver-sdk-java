package org.zend.php.zendserver.deployment.core.debugger;

import java.net.URL;
import java.util.Map;

public interface IDeploymentHelper {

	public static final String DEFAULT_SERVER = "<default-server>"; //$NON-NLS-1$
	public static final int DEPLOY = 0;
	public static final int UPDATE = 1;
	public static final int AUTO_DEPLOY = 2;
	public static final int NO_ACTION = 3;

	URL getBaseURL();

	String getTargetId();

	String getTargetHost();

	int getAppId();

	String getProjectName();

	Map<String, String> getUserParams();

	String getAppName();

	String getInstalledLocation();

	String getVirtualHost();
	
	Map<String, String> getExtraAttributes();

	boolean isIgnoreFailures();

	boolean isDefaultServer();

	int getOperationType();

	boolean isEnabled();
	
	boolean isDevelopmentModeEnabled();
	
	boolean isWarnUpdate();

	void setBaseURL(String baseURL);

	void setTargetId(String targetId);

	void setTargetHost(String targetHost);

	void setAppId(int appId);

	void setProjectName(String projectName);

	void setUserParams(Map<String, String> userParams);

	void setAppName(String appName);

	void setIgnoreFailures(boolean ignoreFailures);

	void setDefaultServer(boolean defaultServer);

	void setOperationType(int type);

	void setInstalledLocation(String location);

	void setEnabled(boolean enabled);
	
	void setDevelopmentMode(boolean enabled);
	
	void setWarnUpdate(boolean enabled);

	void setExtraAtttributes(Map<String, String> extraAttributes);
	
}
