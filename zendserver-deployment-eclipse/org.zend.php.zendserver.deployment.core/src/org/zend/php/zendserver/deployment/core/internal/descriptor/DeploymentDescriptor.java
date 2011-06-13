package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
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
		return get(NAME);
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
		return Collections.unmodifiableList(parameters);
	}
	
	public List<IVariable> getVariables() {
		return Collections.unmodifiableList(variables);
	}
	
	public List<IDependency> getDependencies() {
		return Collections.unmodifiableList(dependencies);
	}
	
	public List<String> getPersistentResources() {
		return Collections.unmodifiableList(persistent);
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
	
	public void addDependency(IDependency dep) {
		add(DEPENDENCIES, dep);
	}
	
	public void removeDependency(int index) {
		remove(DEPENDENCIES, index);
	}
	
	public void setDependency(int index, IDependency dep) {
		set(DEPENDENCIES, index, dep);
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

	private List getList(String key) {
		if (DEPENDENCIES.equals(key)) {
			return dependencies;
		} else if (PARAMETERS.equals(key)) {
			return parameters;
		} else if (PERSISTENT_RESOURCES.equals(key)) {
			return persistent;
		} else if (VARIABLES.equals(key)) {
			return variables;
		}
		
		throw new IllegalArgumentException("Unknown list property name "+key);
	}

	public void add(String key, Object value) {
		getList(key).add(value);
	}
	
	public void remove(String key, int index) {
		getList(key).remove(index);
	}
	
	public void set(String key, int index, Object value) {
		List list = getList(key);
		if (index < list.size()) {
			Object dest = list.get(index);
			if (dest instanceof IModelObject) {
				((IModelObject)dest).copy((IModelObject)value);
			} else {
				list.set(index, value);
			}
		} else {
			list.add(value);
		}
	}
	
	public void set(String key, String value) {
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

	public String get(String key) {
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
