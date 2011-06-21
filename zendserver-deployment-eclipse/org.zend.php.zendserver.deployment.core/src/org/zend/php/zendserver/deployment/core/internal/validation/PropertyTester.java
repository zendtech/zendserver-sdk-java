package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public abstract class PropertyTester {

	private Feature[] properties;
	private int severity;
	
	public PropertyTester(int severity, Feature[] properties) {
		this.severity = severity;
		this.properties = properties;
	}
	
	public void validate(IDeploymentDescriptor target, List<ValidationStatus> statuses) {
		for (Feature key : properties) {
			String message = test(target.get(key));
			if (message != null) {
				statuses.add(new ValidationStatus(key, severity, message));
			}
		}
	}
	
	abstract String test(Object property);
	
}
