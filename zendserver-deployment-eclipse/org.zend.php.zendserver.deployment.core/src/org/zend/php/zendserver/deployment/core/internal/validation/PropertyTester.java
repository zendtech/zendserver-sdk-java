package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;

public abstract class PropertyTester {

	private String[] properties;
	private int severity;
	
	public PropertyTester(int severity, String[] properties) {
		this.severity = severity;
		this.properties = properties;
	}
	
	public void validate(IDeploymentDescriptor target, List<ValidationStatus> statuses) {
		for (String key : properties) {
			String message = test(target.get(key));
			if (message != null) {
				statuses.add(new ValidationStatus(key, severity, message));
			}
		}
	}
	
	abstract String test(Object property);
	
}
