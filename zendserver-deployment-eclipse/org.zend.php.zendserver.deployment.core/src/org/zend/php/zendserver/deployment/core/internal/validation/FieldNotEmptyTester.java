package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;


public class FieldNotEmptyTester extends PropertyTester {

	public FieldNotEmptyTester() {
		super(ValidationStatus.ERROR);
	}

	@Override
	public String test(Feature f, Object property) {
		return property == null || "".equals(((String)property).trim())? Messages.bind(Messages.FieldNotEmptyTester_MustNotBeEmpty, f.xpath) : null; //$NON-NLS-1$
	}

}
