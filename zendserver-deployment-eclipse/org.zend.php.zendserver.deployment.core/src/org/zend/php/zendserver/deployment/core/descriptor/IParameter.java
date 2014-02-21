package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IParameter extends IModelContainer {

	String EMAIL = "email"; //$NON-NLS-1$
	String HOSTNAME = "hostname"; //$NON-NLS-1$
	String CHECKBOX = "checkbox"; //$NON-NLS-1$
	String CHOICE = "choice"; //$NON-NLS-1$
	String NUMBER = "number"; //$NON-NLS-1$
	String STRING = "string"; //$NON-NLS-1$
	String PASSWORD = "password"; //$NON-NLS-1$
	
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
