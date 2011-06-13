package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;

public class Variable implements IVariable {

	private String name;
	
	private String value;
	
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Variable() {
		this("", "");
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		this.value = newValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void copy(IModelObject obj) {
		IVariable src = (IVariable) obj;
		setName(src.getName());
		setValue(src.getValue());
	}
}
