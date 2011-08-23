package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class PasswordValidator implements IParameterValidator {

	private ParametersBlock block;
	private String identicalId;

	public PasswordValidator(ParametersBlock block, String identicalId) {
		this.block = block;
		this.identicalId = identicalId;
	}

	public IStatus validate(String id, String value) {
		Map<String, String> params = block.getHelper().getUserParams();
		String identical = params.get(identicalId);
		if (value != null && value.equals(identical)) {
			return Status.OK_STATUS;
		} else {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
					Messages.PasswordValidator_InvalidPassword, id));
		}
	}

}
