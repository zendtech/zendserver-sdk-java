package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class EmailValidator implements IParameterValidator {

	public IStatus validate(String id, String value) {
		if (value.isEmpty()) {
			return Status.OK_STATUS;
		}
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+"); //$NON-NLS-1$
		Matcher m = p.matcher(value);
		String last = value.substring(value.lastIndexOf(".") + 1); //$NON-NLS-1$
		if (m.matches() && last.length() >= 2 && value.length() - last.length() > 1) {
			return Status.OK_STATUS;
		} else
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
					Messages.EmailValidator_InvalidEmail, id));
	}

}
