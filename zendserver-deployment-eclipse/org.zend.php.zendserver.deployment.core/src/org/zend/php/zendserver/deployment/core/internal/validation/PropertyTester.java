package org.zend.php.zendserver.deployment.core.internal.validation;


public abstract class PropertyTester {

	int severity;
	
	public PropertyTester(int severity) {
		this.severity = severity;
	}
	
	abstract String test(Object property);
	
}
