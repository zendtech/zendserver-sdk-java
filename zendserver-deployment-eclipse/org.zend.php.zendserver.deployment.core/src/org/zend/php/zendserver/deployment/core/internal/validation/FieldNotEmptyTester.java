package org.zend.php.zendserver.deployment.core.internal.validation;


public class FieldNotEmptyTester extends PropertyTester {

	public FieldNotEmptyTester() {
		super(ValidationStatus.ERROR);
	}

	@Override
	String test(Object property) {
		return property == null || "".equals(((String)property).trim())? "Property must not be empty" : null;
	}

}
