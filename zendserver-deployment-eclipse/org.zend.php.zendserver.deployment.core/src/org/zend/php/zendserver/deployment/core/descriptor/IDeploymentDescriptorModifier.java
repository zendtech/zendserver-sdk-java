package org.zend.php.zendserver.deployment.core.descriptor;

import org.eclipse.core.runtime.CoreException;

/**
 * Used to modify deployment descriptor, can be obtained from descriptor container.
 *
 */
public interface IDeploymentDescriptorModifier {

	void set(IDeploymentDescriptor target, String property, String value) throws CoreException;
	
	void setName(String name) throws CoreException;

	void setSummary(String summary) throws CoreException;

	void setDescription(String description) throws CoreException;

	void setReleaseVersion(String version) throws CoreException;
	
	void setApiVersion(String version) throws CoreException;

	void setEulaLocation(String location) throws CoreException;

	void setIconLocation(String location) throws CoreException;

	void setDocumentRoot(String location) throws CoreException;

	void setScriptsRoot(String location) throws CoreException;

	void setHealthcheck(String url) throws CoreException;

	void addParameter(IParameter param) throws CoreException;

	void removeParameter(IParameter param) throws CoreException;
		
	void addVariable(IVariable var) throws CoreException;

	void removeVariable(IVariable var) throws CoreException;

	void setVariableValue(IVariable input, String text) throws CoreException;
	
	void setVariableName(IVariable input, String text) throws CoreException;

	void addDependency(IDependency dep) throws CoreException;

	void removeDependency(IDependency dep) throws CoreException;

	void setDependencyName(IDependency dep, String text) throws CoreException;
	
	void setDependencyMin(IDependency dep, String text) throws CoreException;
	
	void setDependencyMax(IDependency dep, String text) throws CoreException;
	
	void setDependencyEquals(IDependency dep, String text) throws CoreException;
	
	void setDependencyConflicts(IDependency dep, String text) throws CoreException;
	
	void addDependencyExclude(IDependency dep, String text) throws CoreException;
	
	void removeDependencyExclude(IDependency dep, String text) throws CoreException;

	void addPersistentResource(String path) throws CoreException;

	void removePersistentResource(String path) throws CoreException;

	void setParameterRequired(IParameter input, boolean selection) throws CoreException;
	
	void setParameterReadonly(IParameter input, boolean selection) throws CoreException;

	void setParameterId(IParameter input, String text) throws CoreException;

	void setParameterDisplay(IParameter input, String text) throws CoreException;

	void setParameterDefault(IParameter input, String text) throws CoreException;

	void setParameterType(IParameter input, String text) throws CoreException;
	
	void setParameterIdentical(IParameter input, String text) throws CoreException;

	void setParameterDescription(IParameter input, String text) throws CoreException;

	void setParameterValidation(IParameter input, String[] newParams) throws CoreException;
	
	/**
	 * Descriptor which is being modified. Changes are reflected immediately in descriptor
	 * @return
	 */
	IDeploymentDescriptor getDescriptor();

	/**
	 * Set to true to automatically apply changes to underlying document on every change
	 * 
	 * @param autoSave
	 */
	void setAutoSave(boolean autoSave);
	
	/**
	 * Stores changes to underlying document.
	 * 
	 * @throws CoreException
	 */
	void save() throws CoreException;
}
