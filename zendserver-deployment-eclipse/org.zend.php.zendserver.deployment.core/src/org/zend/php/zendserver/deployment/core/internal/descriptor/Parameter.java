package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class Parameter implements IParameter {

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

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	private String id;
	private String type;
	private boolean required;
	private String display;
	private String defaultValue;
	private String description;
	private String longDescription;
	private String[] validValues;
	private String serverType;
	
	public Parameter(String id, String type, boolean required, String display, String defaultValue, String description, String descriptionLong) {
		this.id = id;
		this.type = type;
		this.required = required;
		this.display = display;
		this.defaultValue = defaultValue;
		this.description = description;
		this.longDescription = descriptionLong;
	}

	public Parameter(String id, String type) {
		this(id, type, false, "", "", "", "");
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
	
	public String getLongDescription() {
		return longDescription;
	}
	
	public String getServerType() {
		return serverType;
	}
	
	public String[] getValidValues() {
		return validValues;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
		
	}

	public void setValidValues(String[] array) {
		this.validValues = array;
	}
	
}
