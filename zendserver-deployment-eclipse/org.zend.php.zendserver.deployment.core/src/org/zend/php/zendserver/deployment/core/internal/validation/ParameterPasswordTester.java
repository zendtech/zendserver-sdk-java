package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class ParameterPasswordTester extends PropertyTester {

	public ParameterPasswordTester(int severity) {
		super(severity);
	}

	@Override
	public String test(Feature feature, Object property, IModelObject object) {
		boolean isPassword = IParameter.PASSWORD.equals(object
				.get(DeploymentDescriptorPackage.TYPE));
		boolean notNull = (property != null);
		boolean notEmpty = notNull ? ((String) property).trim().length() > 0 : false;
		return (!isPassword) || (isPassword && notNull && notEmpty) ? null
				: Messages.ParameterPasswordTester_IdenticalIsRequired;
	}

}
