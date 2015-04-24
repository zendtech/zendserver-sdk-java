package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class NumberValidator implements IParameterValidator {

	public IStatus validate(String id, String value) {
		if (!value.isEmpty()) {
			try {
				Integer.valueOf(value);
			} catch (NumberFormatException e) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						MessageFormat.format(
								Messages.NumberValidator_NotANumber, id));
			}
		}
		return Status.OK_STATUS;
	}

}
