package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;


public abstract class PropertyTester {

	int severity;
	
	public PropertyTester(int severity) {
		this.severity = severity;
	}
	
	public abstract String test(Feature feature, Object property, IModelObject object);
	
}
