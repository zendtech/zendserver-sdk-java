package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IVariable;

public class Variable implements IVariable {

	private String value;
	
	public Variable(String value) {
		this.value = value;
	}

	public Variable() {
		this("");
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		this.value = newValue;
	}
	
}
