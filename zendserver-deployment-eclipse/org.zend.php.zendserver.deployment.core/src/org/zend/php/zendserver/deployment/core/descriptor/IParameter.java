package org.zend.php.zendserver.deployment.core.descriptor;

public interface IParameter {

	String getId();

	String getType();

	boolean isRequired();

	String getDisplay();
	
	String getDefaultValue();

	String getDescription();

	String getLongDescription();

	String[] getValidValues();

	String getServerType();
}
