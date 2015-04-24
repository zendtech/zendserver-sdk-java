package org.zend.php.zendserver.deployment.core.debugger;

public enum DeploymentAttributes {
	
	ENABLED("enabled"), //$NON-NLS-1$

	BASE_URL("baseURL"), //$NON-NLS-1$
	
	TARGET_ID("targetId"), //$NON-NLS-1$
	
	TARGET_HOST("targetHost"),  //$NON-NLS-1$
	
	APP_ID("appId"), //$NON-NLS-1$
	
	PROJECT_NAME("projectName"), //$NON-NLS-1$
	
	PARAMETERS("userParams"), //$NON-NLS-1$
	
	APPLICATION_NAME("applicationName"), //$NON-NLS-1$
	
	IGNORE_FAILURES("ignoreFailures"), //$NON-NLS-1$
	
	DEFAULT_SERVER("defaultServer"), //$NON-NLS-1$
	
	OPERATION_TYPE("operationType"), //$NON-NLS-1$
	
	INSTALLED_LOCATION("installedLocation"), //$NON-NLS-1$
	
	DEVELOPMENT_MODE("developmentMode"), //$NON-NLS-1$
	
	WARN_UPDATE("warnUpdate"); //$NON-NLS-1$
	
	private static final String PREFIX = "org.zend.php.zendserver.deployment."; //$NON-NLS-1$
	
	private String name;
	
	private DeploymentAttributes(String name) {
		this.name = PREFIX + name;
	}
	
	public String getName() {
		return name;
	}
	
}
