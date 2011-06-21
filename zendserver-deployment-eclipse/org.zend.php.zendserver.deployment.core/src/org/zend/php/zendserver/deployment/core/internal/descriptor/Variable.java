package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;

public class Variable extends ModelObject implements IVariable {

	private String name;
	
	private String value;
	
	public Variable() {
		super(new Feature[] {
				VALUE,
				NAME
		});
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

	public void set(Feature key, String value) {
		if (NAME.equals(key)) {
			this.name = value;
		} else if (VALUE.equals(key)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Unknown Variable property to set: "+key);
		}
	}

	public String get(Feature key) {
		if (NAME.equals(key)) {
			return name;
		} else if (VALUE.equals(key)) {
			return value;
		} else {
			throw new IllegalArgumentException("Unknown Variable property to get: "+key);
		}
	}
}
