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
		this.name = name;
		fireChange(NAME);
	}
	
	public String getReleaseVersion() {
		return releaseVersion;
	}
	
	public void setReleaseVersion(String version) {
		this.releaseVersion = version;
		fireChange(VERSION_RELEASE);
	}
	
	public String getEulaLocation() {
		return eulaLocation;
	}
	
	public void setEulaLocation(String eulaLocation) {
		this.eulaLocation = eulaLocation;
		fireChange(EULA);
	}
	
	public String getIconLocation() {
		return iconLocation;
	}
	public void setIconLocation(String iconLocation) {
		this.iconLocation = iconLocation;
		fireChange(ICON);
	}
	
	public String getDocumentRoot() {
		return documentRoot;
	}
	
	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
		fireChange(DOCROOT);
	}
	
	public String getScriptsRoot() {
		return scriptsRoot;
	}
	
	public void setScriptsRoot(String scriptsRoot) {
		this.scriptsRoot = scriptsRoot;
		fireChange(SCRIPTSDIR);
	}
	
	public String getHealthcheck() {
		return healthcheck;
	}
	
	public void setHealthcheck(String healthcheck) {
		this.healthcheck = healthcheck;
		fireChange(HEALTHCHECK);
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getDescription() {
		return description;
	}
	public void setSummary(String summary) {
		this.summary = summary;
		fireChange(SUMMARY);
	}
	
	public void setDescription(String description) {
		this.description = description;
		fireChange(DESCRIPTION);
	}
	
	public void setApiVersion(String value) {
		this.apiVersion = value;
		fireChange(VERSION_API);
	}
	
	public String getApiVersion() {
		return apiVersion;
	}

	public String getApplicationDir() {
		return appDir;
	}
	
	public void setApplicationDir(String appDir) {
		this.appDir = appDir;
		fireChange(APPDIR);
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
		
	public void set(Feature key, String value) {
		if (NAME.equals(key)) {
			setName(value);
		} else if (SUMMARY.equals(key)) {
			setSummary(value);
		} else if (DESCRIPTION.equals(key)) {
			setDescription(value);
		} else if (VERSION_RELEASE.equals(key)) {
			setReleaseVersion(value);
		} else if (VERSION_API.equals(key)) {
			setApiVersion(value);
		} else if (EULA.equals(key)) {
			setEulaLocation(value);
		} else if (ICON.equals(key)) {
			setIconLocation(value);
		} else if (DOCROOT.equals(key)) {
			setDocumentRoot(value);
		} else if (SCRIPTSDIR.equals(key)) {
			setScriptsRoot(value);
		} else if (HEALTHCHECK.equals(key)) {
			setHealthcheck(value);
		} else if (APPDIR.equals(key)) {
			setApplicationDir(value);
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
