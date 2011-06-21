package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class FieldNotEmptyTester extends PropertyTester {

	public FieldNotEmptyTester(Feature[] strings) {
		super(ValidationStatus.ERROR, strings);
	}

	@Override
	String test(Object property) {
		return property == null || "".equals(((String)property).trim())? "Property must be set" : null;
	}

}
