package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IDeploymentDescriptor {

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
}
