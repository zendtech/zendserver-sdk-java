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
	private String version = "";
	private String eulaLocation = "";
	private String iconLocation = "";
	private String documentRoot = "";
	private String scriptsRoot = "";
	private String healthcheck = "";
	
	private List<IParameter> parameters = new ArrayList<IParameter>();
	private List<IVariable> variables = new ArrayList<IVariable>();
	private List<IDependency> dependencies = new ArrayList<IDependency>();
	private List<String> noKeepReDeployment = new ArrayList<String>();
	private List<String> noKeepRemoval = new ArrayList<String>();
	private String apiVersion;
	
	public DeploymentDescriptor() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReleaseVersion() {
		return version;
	}
	public void setReleaseVersion(String version) {
		this.version = version;
	}
	public String getEulaLocation() {
		return eulaLocation;
	}
	public void setEulaLocation(String eulaLocation) {
		this.eulaLocation = eulaLocation;
	}
	public String getIconLocation() {
		return iconLocation;
	}
	public void setIconLocation(String iconLocation) {
		this.iconLocation = iconLocation;
	}
	public String getDocumentRoot() {
		return documentRoot;
	}
	public void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}
	public String getScriptsRoot() {
		return scriptsRoot;
	}
	public void setScriptsRoot(String scriptsRoot) {
		this.scriptsRoot = scriptsRoot;
	}
	public String getHealthcheck() {
		return healthcheck;
	}
	public void setHealthcheck(String healthcheck) {
		this.healthcheck = healthcheck;
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
		return Collections.unmodifiableList(noKeepReDeployment);
	}
	public List<String> getNoKeepRemoval() {
		return Collections.unmodifiableList(noKeepRemoval);
	}
	public String getSummary() {
		return summary;
	}
	public String getDescription() {
		return description;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> setPersistentResources() {
		return this.noKeepReDeployment;
	}
	
	public void setApiVersion(String value) {
		this.apiVersion = value;
	}
	
	public String getApiVersion() {
		return apiVersion;
	}
	

}
