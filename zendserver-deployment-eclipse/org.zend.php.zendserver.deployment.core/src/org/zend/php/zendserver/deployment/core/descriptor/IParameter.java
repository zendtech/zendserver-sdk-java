package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IParameter extends IModelContainer {

	Feature DISPLAY = new Feature(null, "display", String.class);
	Feature REQUIRED = new Feature(null, "required", Boolean.class);
	Feature READONLY = new Feature(null, "readonly", Boolean.class);
	Feature TYPE = new Feature(null, "type", String.class);
	Feature IDENTICAL = new Feature(null, "identical", String.class);
	Feature ID = new Feature(null, "id", String.class);
	Feature DEFAULTVALUE = new Feature("defaultvalue", null, String.class);
	Feature DESCRIPTION = new Feature("description", null, String.class);
	Feature VALIDATION = new Feature("validation/enums/enum", null, String.class);
	
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
