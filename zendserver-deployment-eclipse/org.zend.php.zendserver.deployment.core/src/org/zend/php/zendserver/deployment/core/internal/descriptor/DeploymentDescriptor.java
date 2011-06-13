package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;


public class DeploymentDescriptor implements IDeploymentDescriptor {

	private String name = "";
	private String summary = "";
	private String description = "";
	private String releaseVersion = "";
	private String apiVersion;
	private String eulaLocation = "";
	private String iconLocation = "";
	private String documentRoot = "";
	private String appDir;
	private String scriptsRoot = "";
	private String healthcheck = "";
	
	private List<IParameter> parameters = new ArrayList<IParameter>();
	private List<IVariable> variables = new ArrayList<IVariable>();
	private List<IDependency> dependencies = new ArrayList<IDependency>();
	private List<String> persistent = new ArrayList<String>();
	
	public DeploymentDescriptor() {
	}
	
	public String getName() {
		return get(PACKAGE_NAME);
	}
	public void setName(String name) {
		set(PACKAGE_NAME, name);
	}
	public String getReleaseVersion() {
		return get(PACKAGE_VERSION_RELEASE);
	}
	public void setReleaseVersion(String version) {
		set(PACKAGE_VERSION_RELEASE, version);
	}
	public String getEulaLocation() {
		return get(PACKAGE_EULA);
	}
	public void setEulaLocation(String eulaLocation) {
		set(PACKAGE_EULA, eulaLocation);
	}
	public String getIconLocation() {
		return get(PACKAGE_ICON);
	}
	public void setIconLocation(String iconLocation) {
		set(PACKAGE_ICON, iconLocation);
	}
	public String getDocumentRoot() {
		return get(PACKAGE_DOCROOT);
	}
	public void setDocumentRoot(String documentRoot) {
		set(PACKAGE_DOCROOT, documentRoot);
	}
	public String getScriptsRoot() {
		return get(PACKAGE_SCRIPTSDIR);
	}
	public void setScriptsRoot(String scriptsRoot) {
		set(PACKAGE_SCRIPTSDIR, scriptsRoot);
	}
	public String getHealthcheck() {
		return get(PACKAGE_HEALTHCHECK);
	}
	public void setHealthcheck(String healthcheck) {
		set(PACKAGE_HEALTHCHECK, healthcheck);
	}
	public List<IParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	public List<IParameter> setParameters() {
		return this.parameters;
	}
	public List<IVariable> getVariables() {
		return Collections.unmodifiableList(variables);
	}
	public List<IVariable> setVariables() {
		return this.variables;
	}
	public List<IDependency> getDependencies() {
		return Collections.unmodifiableList(dependencies);
	}
	public List<IDependency> setDependencies() {
		return this.dependencies;
	}
	public List<String> getPersistentResources() {
		return Collections.unmodifiableList(persistent);
	}
	public String getSummary() {
		return get(PACKAGE_SUMMARY);
	}
	public String getDescription() {
		return get(PACKAGE_DESCRIPTION);
	}
	public void setSummary(String summary) {
		set(PACKAGE_SUMMARY, summary);
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> setPersistentResources() {
		return this.persistent;
	}
	
	public void setApiVersion(String value) {
		set(PACKAGE_VERSION_API, value);
	}
	
	public String getApiVersion() {
		return get(PACKAGE_VERSION_API);
	}

	public String getApplicationDir() {
		return get(PACKAGE_APPDIR);
	}
	
	public void add(String key, Object value) {
		
	}
	
	public void remove(String key) {
		
	}
	
	public void setApplicationDir(String appDir) {
		set(PACKAGE_APPDIR, appDir);
	}

	public void set(String key, String value) {
		if (PACKAGE_NAME.equals(key)) {
			name = value;
		} else if (PACKAGE_SUMMARY.equals(key)) {
			summary = value;
		} else if (PACKAGE_DESCRIPTION.equals(key)) {
			description = value;
		} else if (PACKAGE_VERSION_RELEASE.equals(key)) {
			releaseVersion = value;
		} else if (PACKAGE_VERSION_API.equals(key)) {
			apiVersion = value;
		} else if (PACKAGE_EULA.equals(key)) {
			eulaLocation = value;
		} else if (PACKAGE_ICON.equals(key)) {
			iconLocation = value;
		} else if (PACKAGE_DOCROOT.equals(key)) {
			documentRoot = value;
		} else if (PACKAGE_SCRIPTSDIR.equals(key)) {
			scriptsRoot = value;
		} else if (PACKAGE_HEALTHCHECK.equals(key)) {
			healthcheck = value;
		} else if (PACKAGE_APPDIR.equals(key)) {
			appDir = value;
		} else {
			throw new IllegalArgumentException("Can't set unknown property "+key);
		}
	}

	public String get(String key) {
		if (PACKAGE_NAME.equals(key)) {
			return name;
		} else if (PACKAGE_SUMMARY.equals(key)) {
			return summary;
		} else if (PACKAGE_DESCRIPTION.equals(key)) {
			return description;
		} else if (PACKAGE_VERSION_RELEASE.equals(key)) {
			return releaseVersion;
		} else if (PACKAGE_VERSION_API.equals(key)) {
			return apiVersion;
		} else if (PACKAGE_EULA.equals(key)) {
			return eulaLocation;
		} else if (PACKAGE_ICON.equals(key)) {
			return iconLocation;
		} else if (PACKAGE_DOCROOT.equals(key)) {
			return documentRoot;
		} else if (PACKAGE_SCRIPTSDIR.equals(key)) {
			return scriptsRoot;
		} else if (PACKAGE_HEALTHCHECK.equals(key)) {
			return healthcheck;
		} else if (PACKAGE_APPDIR.equals(key)) {
			return appDir;
		} else {
			throw new IllegalArgumentException("Can't get unknown property "+key);
		}
	}
	

}
