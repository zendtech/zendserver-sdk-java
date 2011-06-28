package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IParameter extends IModelContainer {

	String EMAIL = "email";
	String HOSTNAME = "hostname";
	String CHECKBOX = "checkbox";
	String CHOICE = "choice";
	String NUMBER = "number";
	String STRING = "string";
	String PASSWORD = "password";
	
	String getId();

	void setId(String id);
	
	String getType();
	
	void setType(String type);

	boolean isRequired();
	
	void setRequired(boolean isRequired);
	
	boolean isReadOnly();
	
	void setReadOnly(boolean isReadonly);

	String getDisplay();
	
	void setDisplay(String display);

	String getIdentical();
	
	void setIdentical(String identical);
	
	String getDefaultValue();
	
	void setDefaultValue(String value);

	String getDescription();
	
	void setDescription(String description);

	List<String> getValidValues();
}
