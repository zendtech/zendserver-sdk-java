package org.zend.php.zendserver.deployment.core.descriptor;

public interface IParameter {

	String EMAIL = "email";
	String HOSTNAME = "hostname";
	String CHECKBOX = "checkbox";
	String CHOICE = "choice";
	String NUMBER = "number";
	String STRING = "string";
	String PASSWORD = "password";
	
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
