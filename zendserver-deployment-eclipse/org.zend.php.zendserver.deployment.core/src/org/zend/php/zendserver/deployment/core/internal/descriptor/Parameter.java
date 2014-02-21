package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.List;

import org.zend.php.zendserver.deployment.core.Messages;
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
		boolean oldValue = this.readOnly;
		this.readOnly = value;
		fireChange(DeploymentDescriptorPackage.READONLY, value, oldValue);
	}
	
	public String getIdentical() {
		return identical;
	}
	
	public void setIdentical(String identical) {
		String oldValue = this.identical;
		this.identical = identical;
		fireChange(DeploymentDescriptorPackage.IDENTICAL, identical, oldValue);
	}

	public void setId(String id) {
		String oldValue = this.id;
		this.id = id;
		fireChange(DeploymentDescriptorPackage.ID, id, oldValue);
	}

	public void setType(String type) {
		String oldValue = this.type;
		this.type = type;
		fireChange(DeploymentDescriptorPackage.TYPE, type, oldValue);
	}

	public void setRequired(boolean required) {
		boolean oldValue = this.required;
		this.required = required;
		fireChange(DeploymentDescriptorPackage.REQUIRED, required, oldValue);
	}

	public void setDisplay(String display) {
		String oldValue = this.display;
		this.display = display;
		fireChange(DeploymentDescriptorPackage.DISPLAY, display, oldValue);
	}

	public void setDefaultValue(String defaultValue) {
		String oldValue = this.defaultValue;
		this.defaultValue = defaultValue;
		fireChange(DeploymentDescriptorPackage.DEFAULTVALUE, defaultValue, oldValue);
	}

	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		fireChange(DeploymentDescriptorPackage.PARAM_DESCRIPTION, description, oldValue);
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
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownSetDependency, key));
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
				throw new IllegalArgumentException(MessageFormat.format(
						Messages.ZendServerDependency_UnknownGetDependency, key));
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
		return "Parameter [id=" + id + ", type=" + type + ", required=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ required + ", readOnly=" + readOnly + ", display=" + display //$NON-NLS-1$ //$NON-NLS-2$
				+ ", defaultValue=" + defaultValue + ", description=" //$NON-NLS-1$ //$NON-NLS-2$
				+ description + ", identical=" + identical + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean isChildrenFirst() {
		return true;
	}

}
