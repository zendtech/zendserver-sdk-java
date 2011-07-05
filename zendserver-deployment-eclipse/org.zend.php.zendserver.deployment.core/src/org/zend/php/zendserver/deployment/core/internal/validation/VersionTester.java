package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.regex.Pattern;

public class VersionTester extends PropertyTester {

	private Pattern pattern = Pattern.compile("[0-9]*(\\.[0-9]+){0,3}");
	
	public VersionTester() {
		super(ValidationStatus.ERROR);
	}

	@Override
	String test(Object property) {
		if (property == null) {
			return null;
		}
		
		String value = (String) property;
		
		if (pattern.matcher(value).matches()) {
			return null;
		}
		
		return "Property is not a valid version number.";
	}	
}
