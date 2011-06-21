package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.descriptor.IZendComponentDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;


public class DeploymentDescriptor extends ModelContainer implements IDeploymentDescriptor {

	private String name;
	private String summary;
	private String description;
	private String releaseVersion;
	private String apiVersion;
	private String eulaLocation;
	private String iconLocation;
	private String documentRoot;
	private String appDir;
	private String scriptsRoot;
	private String healthcheck;
	
	public DeploymentDescriptor() {
		super(new Feature[] {
				NAME,
				SUMMARY, 
				DESCRIPTION, 
				VERSION_RELEASE, 
				VERSION_API, 
				EULA, 
				ICON, 
				DOCROOT, 
				SCRIPTSDIR, 
				HEALTHCHECK,
				APPDIR
		},
		new Feature[] {
			DEPENDENCIES_PHP,
			DEPENDENCIES_DIRECTIVE,
			DEPENDENCIES_EXTENSION,
			DEPENDENCIES_ZENDFRAMEWORK,
			DEPENDENCIES_ZENDSERVER,
			DEPENDENCIES_ZSCOMPONENT,
			PARAMETERS,
			VARIABLES,
			PERSISTENT_RESOURCES
		});
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		set(NAME, name);
	}
	public String getReleaseVersion() {
		return get(VERSION_RELEASE);
	}
	public void setReleaseVersion(String version) {
		set(VERSION_RELEASE, version);
	}
	public String getEulaLocation() {
		return get(EULA);
	}
	public void setEulaLocation(String eulaLocation) {
		set(EULA, eulaLocation);
	}
	public String getIconLocation() {
		return get(ICON);
	}
	public void setIconLocation(String iconLocation) {
		set(ICON, iconLocation);
	}
	public String getDocumentRoot() {
		return get(DOCROOT);
	}
	public void setDocumentRoot(String documentRoot) {
		set(DOCROOT, documentRoot);
	}
	public String getScriptsRoot() {
		return get(SCRIPTSDIR);
	}
	public void setScriptsRoot(String scriptsRoot) {
		set(SCRIPTSDIR, scriptsRoot);
	}
	public String getHealthcheck() {
		return get(HEALTHCHECK);
	}
	public void setHealthcheck(String healthcheck) {
		set(HEALTHCHECK, healthcheck);
	}
	
	public List<IParameter> getParameters() {
		return super.getList(PARAMETERS);
	}
	
	public List<IVariable> getVariables() {
		return super.getList(VARIABLES);
	}
	
	public List<IPHPDependency> getPHPDependencies() {
		return super.getList(DEPENDENCIES_PHP);
	}
	
	public List<IDirectiveDependency> getDirectiveDependencies() {
		return super.getList(DEPENDENCIES_DIRECTIVE);
	}
	
	public List<IExtensionDependency> getExtensionDependencies() {
		return super.getList(DEPENDENCIES_EXTENSION);
	}
	
	public List<IZendServerDependency> getZendServerDependencies() {
		return super.getList(DEPENDENCIES_ZENDSERVER);
	}
	
	public List<IZendComponentDependency> getZendComponentDependencies() {
		return super.getList(DEPENDENCIES_ZSCOMPONENT);
	}
	
	public List<IZendFrameworkDependency> getZendFrameworkDependencies() {
		return super.getList(DEPENDENCIES_ZENDFRAMEWORK);
	}
	
	public List<String> getPersistentResources() {
		return super.getList(PERSISTENT_RESOURCES);
	}
	public String getSummary() {
		return get(SUMMARY);
	}
	public String getDescription() {
		return get(DESCRIPTION);
	}
	public void setSummary(String summary) {
		set(SUMMARY, summary);
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addPersistentResource(String resource) {
		add(PERSISTENT_RESOURCES, resource);
	}
	
	public void removePersistentResource(int index) {
		remove(PERSISTENT_RESOURCES, index);
	}
	
	public void setPersistentResource(int index, String resource) {
		set(PERSISTENT_RESOURCES, index, resource);
	}
	
	public void addPHPDependency(IPHPDependency dep) {
		add(DEPENDENCIES_PHP, dep);
	}
	
	public void removePHPDependency(int index) {
		remove(DEPENDENCIES_PHP, index);
	}
	
	public void setPHPDependency(int index, IPHPDependency dep) {
		set(DEPENDENCIES_PHP, index, dep);
	}
	
	public void addExtensionDependency(IPHPDependency dep) {
		add(DEPENDENCIES_EXTENSION, dep);
	}
	
	public void removeExtensionDependency(int index) {
		remove(DEPENDENCIES_EXTENSION, index);
	}
	
	public void setExtensionDependency(int index, IPHPDependency dep) {
		set(DEPENDENCIES_EXTENSION, index, dep);
	}
	
	public void addDirectiveDependency(IDirectiveDependency dep) {
		add(DEPENDENCIES_DIRECTIVE, dep);
	}
	
	public void removeDirectiveDependency(int index) {
		remove(DEPENDENCIES_DIRECTIVE, index);
	}
	
	public void setDirectiveDependency(int index, IDirectiveDependency dep) {
		set(DEPENDENCIES_DIRECTIVE, index, dep);
	}
	
	public void addZendServerDependency(IZendServerDependency dep) {
		add(DEPENDENCIES_ZENDSERVER, dep);
	}
	
	public void removeZendServerDependency(int index) {
		remove(DEPENDENCIES_ZENDSERVER, index);
	}
	
	public void setZendServerDependency(int index, IZendServerDependency dep) {
		set(DEPENDENCIES_ZENDSERVER, index, dep);
	}
	
	public void addZendFrameworkDependency(IZendFrameworkDependency dep) {
		add(DEPENDENCIES_ZENDFRAMEWORK, dep);
	}
	
	public void removeZendFrameworkDependency(int index) {
		remove(DEPENDENCIES_ZENDFRAMEWORK, index);
	}
	
	public void setZendFrameworkDependency(int index, IZendFrameworkDependency dep) {
		set(DEPENDENCIES_ZENDFRAMEWORK, index, dep);
	}
	
	public void addZendComponentDependency(IZendComponentDependency dep) {
		add(DEPENDENCIES_ZSCOMPONENT, dep);
	}
	
	public void removeZendComponentDependency(int index) {
		remove(DEPENDENCIES_ZSCOMPONENT, index);
	}
	
	public void setZendComponentDependency(int index, IZendComponentDependency dep) {
		set(DEPENDENCIES_ZSCOMPONENT, index, dep);
	}
	
	public void addVariable(IVariable resource) {
		add(VARIABLES, resource);
	}
	
	public void removeVariable(int index) {
		remove(VARIABLES, index);
	}
	
	public void setVariable(int index, IVariable resource) {
		set(VARIABLES, index, resource);
	}
	
	public void addParameter(IParameter resource) {
		add(PARAMETERS, resource);
	}
	
	public void removeParameter(int index) {
		remove(PARAMETERS, index);
	}
	
	public void setParameter(int index, IParameter resource) {
		set(PARAMETERS, index, resource);
	}
	
	public void setApiVersion(String value) {
		set(VERSION_API, value);
	}
	
	public String getApiVersion() {
		return get(VERSION_API);
	}

	public String getApplicationDir() {
		return get(APPDIR);
	}
	
	public void setApplicationDir(String appDir) {
		set(APPDIR, appDir);
	}

	
	public void set(Feature key, String value) {
		if (NAME.equals(key)) {
			name = value;
		} else if (SUMMARY.equals(key)) {
			summary = value;
		} else if (DESCRIPTION.equals(key)) {
			description = value;
		} else if (VERSION_RELEASE.equals(key)) {
			releaseVersion = value;
		} else if (VERSION_API.equals(key)) {
			apiVersion = value;
		} else if (EULA.equals(key)) {
			eulaLocation = value;
		} else if (ICON.equals(key)) {
			iconLocation = value;
		} else if (DOCROOT.equals(key)) {
			documentRoot = value;
		} else if (SCRIPTSDIR.equals(key)) {
			scriptsRoot = value;
		} else if (HEALTHCHECK.equals(key)) {
			healthcheck = value;
		} else if (APPDIR.equals(key)) {
			appDir = value;
		} else {
			throw new IllegalArgumentException("Can't set unknown property "+key);
		}
	}

	public String get(Feature key) {
		if (NAME.equals(key)) {
			return name;
		} else if (SUMMARY.equals(key)) {
			return summary;
		} else if (DESCRIPTION.equals(key)) {
			return description;
		} else if (VERSION_RELEASE.equals(key)) {
			return releaseVersion;
		} else if (VERSION_API.equals(key)) {
			return apiVersion;
		} else if (EULA.equals(key)) {
			return eulaLocation;
		} else if (ICON.equals(key)) {
			return iconLocation;
		} else if (DOCROOT.equals(key)) {
			return documentRoot;
		} else if (SCRIPTSDIR.equals(key)) {
			return scriptsRoot;
		} else if (HEALTHCHECK.equals(key)) {
			return healthcheck;
		} else if (APPDIR.equals(key)) {
			return appDir;
		} else {
			throw new IllegalArgumentException("Can't get unknown property "+key);
		}
	}
}
