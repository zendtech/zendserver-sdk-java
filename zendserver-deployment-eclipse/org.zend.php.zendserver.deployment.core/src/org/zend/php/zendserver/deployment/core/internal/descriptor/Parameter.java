package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class Parameter implements IParameter {

	private String id;
	private String type;
	private boolean required;
	private boolean readOnly;
	private String display;
	private String defaultValue;
	private String description;
	private String[] validValues;
	private String identical;
	
	public Parameter(String id, String type, boolean required, boolean readOnly, String display, String defaultValue, String description, String identical) {
		this.id = id;
		this.type = type;
		this.required = required;
		this.readOnly = readOnly;
		this.display = display;
		this.defaultValue = defaultValue;
		this.description = description;
		this.identical = identical;
	}

	public Parameter(String id, String type) {
		this(id, type, false, false, "", "", "", null);
	}
	
	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDisplay() {
		return display;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}
	
	public String[] getValidValues() {
		return validValues;
	}

	public void setValidValues(String[] array) {
		this.validValues = array;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean value) {
		this.readOnly = value;
	}
	
	public String getIdentical() {
		return identical;
	}
	
	public void setIdentical(String id) {
		this.identical = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
