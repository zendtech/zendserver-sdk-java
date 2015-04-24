package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;

public interface IParameterValidator {

	IStatus validate(String id, String value);

}
