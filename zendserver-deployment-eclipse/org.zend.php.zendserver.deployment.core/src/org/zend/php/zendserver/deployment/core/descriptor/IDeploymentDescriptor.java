package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IDeploymentDescriptor {

	String PACKAGE = "package";
	String NAME = "package/name";
	String SUMMARY = "package/summary";
	String DESCRIPTION = "package/description";
	String VERSION_RELEASE = "package/version/release";
	String VERSION_API = "package/version/api";
	String EULA = "package/eula";
	String ICON = "package/icon";
	String DOCROOT = "package/docroot";
	String SCRIPTSDIR = "package/scriptsdir";
	String HEALTHCHECK = "package/healthcheck";
	String APPDIR = "package/appdir";
	
	String DEPENDENCIES = "package/dependencies/required";
	String PARAMETERS = "package/parameters/parameter";
	String VARIABLES = "package/variables/variable";
	String PERSISTENT_RESOURCES = "package/persistentresources/resource";
	
	
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
	
	void set(String key, int index, Object value);
	
	void add(String key, Object value);
	
	void remove(String key, int index);
}
