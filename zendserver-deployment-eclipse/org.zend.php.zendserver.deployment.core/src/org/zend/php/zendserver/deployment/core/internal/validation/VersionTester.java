package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.regex.Pattern;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class VersionTester extends PropertyTester {

	private Pattern pattern = Pattern.compile("[0-9]*(\\.[0-9]+){0,3}"); //$NON-NLS-1$
	
	public VersionTester(DescriptorSemanticValidator descriptorSemanticValidator) {
		super(ValidationStatus.ERROR);
	}

	@Override
	public String test(Feature feature, Object property, IModelObject object) {
		if (property == null) {
			return null;
		}
		
		String value = (String) property;
		
		if (pattern.matcher(value).matches()) {
			return null;
		}
		
		return Messages.bind(Messages.VersionTester_IsNotValidVersionNumber, feature.xpath);
	}	
}
