package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IDeploymentDescriptor {

	String PACKAGE = "package";
	String PACKAGE_NAME = "package/name";
	String PACKAGE_SUMMARY = "package/summary";
	String PACKAGE_DESCRIPTION = "package/description";
	String PACKAGE_VERSION_RELEASE = "package/version/release";
	String PACKAGE_VERSION_API = "package/version/api";
	String PACKAGE_EULA = "package/eula";
	String PACKAGE_ICON = "package/icon";
	String PACKAGE_DOCROOT = "package/docroot";
	String PACKAGE_SCRIPTSDIR = "package/scriptsdir";
	String PACKAGE_HEALTHCHECK = "package/healthcheck";
	String PACKAGE_APPDIR = "package/appdir";
	
	String getName();

	String getSummary();

	String getDescription();

	/**
	 * @return Version or null
	 */
	String getReleaseVersion();
	
	String getApiVersion();

	String getEulaLocation();

	String getIconLocation();

	String getDocumentRoot();
	
	String getApplicationDir();

	String getScriptsRoot();

	/**
	 * 
	 * @return URL or null
	 */
	String getHealthcheck();

	List<IParameter> getParameters();

	List<IVariable> getVariables();

	List<IDependency> getDependencies();

	List<String> getPersistentResources();
	
	void set(String key, String value);
	
	String get(String key);
}
