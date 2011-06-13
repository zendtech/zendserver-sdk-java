package org.zend.php.zendserver.deployment.core.internal.validation;

public class FieldNotEmptyTester extends PropertyTester {

	public FieldNotEmptyTester(String[] strings) {
		super(ValidationStatus.ERROR, strings);
	}

	@Override
	String test(Object property) {
		return property == null ? "Property must be set" : null;
	}

}
