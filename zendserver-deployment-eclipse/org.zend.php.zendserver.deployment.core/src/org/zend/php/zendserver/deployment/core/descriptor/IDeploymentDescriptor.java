package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IDeploymentDescriptor extends IModelContainer {

	Feature PACKAGE = new Feature("package", null, IModelObject.class);
	
	Feature NAME = new Feature("name", null, String.class);
	Feature SUMMARY = new Feature("summary", null, String.class);
	Feature DESCRIPTION = new Feature("description", null, String.class);
	Feature VERSION_RELEASE = new Feature("version/release", null, String.class);
	Feature VERSION_API = new Feature("version/api", null, String.class);
	Feature EULA = new Feature("eula", null, String.class);
	Feature ICON = new Feature("icon", null, String.class);
	Feature DOCROOT = new Feature("docroot", null, String.class);
	Feature SCRIPTSDIR = new Feature("scriptsdir", null, String.class);
	Feature HEALTHCHECK = new Feature("healthcheck", null, String.class);
	Feature APPDIR = new Feature("appdir", null, String.class);
	
	Feature DEPENDENCIES_PHP = new Feature("dependencies/required/php", null, IModelObject.class);
	Feature DEPENDENCIES_EXTENSION = new Feature("dependencies/required/extension", null, IModelObject.class);
	Feature DEPENDENCIES_DIRECTIVE = new Feature("dependencies/required/directive", null, IModelObject.class);
	Feature DEPENDENCIES_ZENDSERVER = new Feature("dependencies/required/zendserver", null, IModelObject.class);
	Feature DEPENDENCIES_ZENDFRAMEWORK = new Feature("dependencies/required/zendframework", null, IModelObject.class);
	Feature DEPENDENCIES_ZSCOMPONENT = new Feature("dependencies/required/zendservercomponent", null, IModelObject.class);
	Feature PARAMETERS = new Feature("parameters/parameter", null, IModelObject.class);
	Feature VARIABLES = new Feature("variables/variable", null, IModelObject.class);
	Feature PERSISTENT_RESOURCES = new Feature("persistentresources/resource", null, String.class);
	
	String getName();

	void setName(String name);
	
	String getSummary();
	
	void setSummary(String summary);

	String getDescription();
	
	void setDescription(String descr);

	String getReleaseVersion();
	
	void setReleaseVersion(String ver);
	
	String getApiVersion();

	void setApiVersion(String ver);
	
	String getEulaLocation();
	
	void setEulaLocation(String value);

	String getIconLocation();
	
	void setIconLocation(String value);

	String getDocumentRoot();
	
	void setDocumentRoot(String folder);
	
	String getApplicationDir();
	
	void setApplicationDir(String value);

	String getScriptsRoot();

	void setScriptsRoot(String value);
	
	/**
	 * 
	 * @return URL or null
	 */
	String getHealthcheck();

	List<IParameter> getParameters();

	List<IVariable> getVariables();

	List<IPHPDependency> getPHPDependencies();
	
	List<IDirectiveDependency> getDirectiveDependencies();
	
	List<IExtensionDependency> getExtensionDependencies();
	
	List<IZendServerDependency> getZendServerDependencies();
	
	List<IZendFrameworkDependency> getZendFrameworkDependencies();
	
	List<IZendComponentDependency> getZendComponentDependencies();

	List<String> getPersistentResources();
}
