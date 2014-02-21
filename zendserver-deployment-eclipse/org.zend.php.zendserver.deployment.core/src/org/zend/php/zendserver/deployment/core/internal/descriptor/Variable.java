package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;

public class Variable extends ModelObject implements IVariable {

	private String name;
	
	private String value;
	
	public Variable() {
		super(new Feature[] {
				DeploymentDescriptorPackage.VALUE,
				DeploymentDescriptorPackage.VAR_NAME
		});
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		String oldValue = this.value;
		this.value = newValue;
		fireChange(DeploymentDescriptorPackage.VALUE, newValue, oldValue);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		fireChange(DeploymentDescriptorPackage.VAR_NAME, name, oldValue);
	}
	
	public void copy(IModelObject obj) {
		IVariable src = (IVariable) obj;
		setName(src.getName());
		setValue(src.getValue());
	}

	public void set(Feature key, String value) {
		switch (key.id) {
		case DeploymentDescriptorPackage.VAR_NAME_ID:
			setName(value);
			break;
		case DeploymentDescriptorPackage.VALUE_ID:
			setValue(value);
			break;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownSetDependency, key));
		}
	}

	public String get(Feature key) {
		switch (key.id) {
		case DeploymentDescriptorPackage.VAR_NAME_ID:
			return name;
		case DeploymentDescriptorPackage.VALUE_ID:
			return value;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownGetDependency, key));
		}
	}
	
}
