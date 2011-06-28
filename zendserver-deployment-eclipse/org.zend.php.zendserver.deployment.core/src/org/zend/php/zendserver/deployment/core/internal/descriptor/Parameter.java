package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
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
				DeploymentDescriptorPackage.DISPLAY,
				DeploymentDescriptorPackage.REQUIRED,
				DeploymentDescriptorPackage.READONLY,
				DeploymentDescriptorPackage.TYPE,
				DeploymentDescriptorPackage.IDENTICAL, 
				DeploymentDescriptorPackage.ID,
				DeploymentDescriptorPackage.DEFAULTVALUE,
				DeploymentDescriptorPackage.PARAM_DESCRIPTION,
		}, new Feature[] {
				DeploymentDescriptorPackage.VALIDATION
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
		return super.getList(DeploymentDescriptorPackage.VALIDATION);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean value) {
		this.readOnly = value;
		fireChange(DeploymentDescriptorPackage.READONLY, value);
	}
	
	public String getIdentical() {
		return identical;
	}
	
	public void setIdentical(String id) {
		this.identical = id;
		fireChange(DeploymentDescriptorPackage.IDENTICAL, id);
	}

	public void setId(String id) {
		this.id = id;
		fireChange(DeploymentDescriptorPackage.ID, id);
	}

	public void setType(String type) {
		this.type = type;
		fireChange(DeploymentDescriptorPackage.TYPE, type);
	}

	public void setRequired(boolean required) {
		this.required = required;
		fireChange(DeploymentDescriptorPackage.REQUIRED, required);
	}

	public void setDisplay(String display) {
		this.display = display;
		fireChange(DeploymentDescriptorPackage.DISPLAY, display);
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		fireChange(DeploymentDescriptorPackage.DEFAULTVALUE, defaultValue);
	}

	public void setDescription(String description) {
		this.description = description;
		fireChange(DeploymentDescriptorPackage.PARAM_DESCRIPTION, description);
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
		switch (key.id) {
		case DeploymentDescriptorPackage.REQUIRED_ID: 
			setRequired(value);
			break;
		case DeploymentDescriptorPackage.READONLY_ID: 
			setReadOnly(value);
			break;
		default:
			throw new IllegalArgumentException("Unknown parametery property to set: "+key);
		}
	}
	
	public void set(Feature key, String value) {
		switch (key.id) {
			case DeploymentDescriptorPackage.DISPLAY_ID: 
				setDisplay(value);
				break;
			case DeploymentDescriptorPackage.TYPE_ID: 
				setType(value);
				break;
			case DeploymentDescriptorPackage.IDENTICAL_ID: 
				setIdentical(value);
				break;
			case DeploymentDescriptorPackage.ID_ID: 
				setId(value);
				break;
			case DeploymentDescriptorPackage.DEFAULTVALUE_ID: 
				setDefaultValue(value);
				break;
			case DeploymentDescriptorPackage.PARAM_DESCRIPTION_ID: 
				setDescription(value);
				break;
			default:
				set(key, Boolean.parseBoolean(value));
		}
	}

	public boolean getBoolean(Feature key) {
		switch (key.id) {
			case DeploymentDescriptorPackage.REQUIRED_ID: 
				return required;
			case DeploymentDescriptorPackage.READONLY_ID: 
				return readOnly;
			default:
				throw new IllegalArgumentException("Unknown parametery property to set: "+key);
		}
	}
	
	public String get(Feature key) {
		switch (key.id) {
		case DeploymentDescriptorPackage.DISPLAY_ID: 
			return display;
		case DeploymentDescriptorPackage.TYPE_ID: 
			return type;
		case DeploymentDescriptorPackage.IDENTICAL_ID: 
			return identical;
		case DeploymentDescriptorPackage.ID_ID: 
			return id;
		case DeploymentDescriptorPackage.DEFAULTVALUE_ID: 
			return defaultValue;
		case DeploymentDescriptorPackage.PARAM_DESCRIPTION_ID: 
			return description;
		}
		
		return Boolean.toString(getBoolean(key));
	}
	
	@Override
	public String toString() {
		return "Parameter [id=" + id + ", type=" + type + ", required="
				+ required + ", readOnly=" + readOnly + ", display=" + display
				+ ", defaultValue=" + defaultValue + ", description="
				+ description + ", identical=" + identical + "]";
	}

}
