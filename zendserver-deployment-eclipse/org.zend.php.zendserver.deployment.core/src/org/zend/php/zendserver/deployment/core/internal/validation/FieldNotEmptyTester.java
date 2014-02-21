package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;


public class FieldNotEmptyTester extends PropertyTester {

	public FieldNotEmptyTester(DescriptorSemanticValidator descriptorSemanticValidator) {
		super(ValidationStatus.ERROR);
	}

	@Override
	public String test(Feature f, Object property, IModelObject object) {
		String fieldName = f.xpath != null ? f.xpath : f.attrName; // TODO need better strategy for getting XML names
		return property == null || "".equals(((String)property).trim())? Messages.bind(Messages.FieldNotEmptyTester_MustNotBeEmpty, fieldName) : null; //$NON-NLS-1$
	}

}
