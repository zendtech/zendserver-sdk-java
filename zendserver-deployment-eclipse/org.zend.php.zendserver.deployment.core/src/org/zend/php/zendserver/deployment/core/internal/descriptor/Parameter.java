package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class Parameter  extends ModelContainer implements IParameter {

	private String id;
	private String type;
	private boolean required;
	private boolean readOnly;
	private String display;
	private String defaultValue;
	private String description;
	private String identical;
	
	public Parameter() {
		super(new Feature[] {
				DISPLAY,
				REQUIRED,
				READONLY,
				TYPE,
				IDENTICAL, 
				ID,
				DEFAULTVALUE,
				DESCRIPTION,
		}, new Feature[] {
				VALIDATION
		});
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
	
	public List<String> getValidValues() {
		return super.getList(VALIDATION);
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
	
	public void copy(IModelObject obj) {
		IParameter src = (IParameter) obj;
		setDefaultValue(src.getDefaultValue());
		setDescription(src.getDefaultValue());
		setDisplay(src.getDisplay());
		setId(src.getId());
		setRequired(src.isRequired());
		setReadOnly(src.isReadOnly());
		setType(src.getType());
		setIdentical(src.getIdentical());
	}

	public void set(Feature key, boolean value) {
		if (REQUIRED.equals(key)) {
			setRequired(value);
		} else if (READONLY.equals(key)) {
			setReadOnly(value);
		} else throw new IllegalArgumentException("Unknown parametery property to set: "+key);
	}
	
	public void set(Feature key, String value) {
		if (DISPLAY.equals(key)) {
			setDisplay(value);
		} else if (TYPE.equals(key)) {
			setType(value);
		} else if (IDENTICAL.equals(key)) {
			setIdentical(value);
		} else if (ID.equals(key)) {
			setId(value);
		} else if (DEFAULTVALUE.equals(key)) {
			setDefaultValue(value);
		} else if (DESCRIPTION.equals(key)) {
			setDescription(value);
		} else set(key, Boolean.parseBoolean(value));
	}

	public boolean getBoolean(Feature key) {
		if (REQUIRED.equals(key)) {
			return required;
		} else if (READONLY.equals(key)) {
			return readOnly;
		} else throw new IllegalArgumentException("Unknown parametery property to set: "+key);
	}
	
	public String get(Feature key) {
		if (DISPLAY.equals(key)) {
			return getDisplay();
		} else if (TYPE.equals(key)) {
			return getType();
		} else if (IDENTICAL.equals(key)) {
			return getIdentical();
		} else if (ID.equals(key)) {
			return getId();
		} else if (DEFAULTVALUE.equals(key)) {
			return getDefaultValue();
		} else if (DESCRIPTION.equals(key)) {
			return getDescription();
		} else return Boolean.toString(getBoolean(key));
	}
	
	@Override
	public String toString() {
		return "Parameter [id=" + id + ", type=" + type + ", required="
				+ required + ", readOnly=" + readOnly + ", display=" + display
				+ ", defaultValue=" + defaultValue + ", description="
				+ description + ", identical=" + identical + "]";
	}

}
