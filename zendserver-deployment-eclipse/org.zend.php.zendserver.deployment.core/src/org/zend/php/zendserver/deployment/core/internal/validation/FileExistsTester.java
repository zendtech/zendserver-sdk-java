package org.zend.php.zendserver.deployment.core.internal.validation;

import java.io.File;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class FileExistsTester extends PropertyTester {

	public FileExistsTester() {
		super(ValidationStatus.ERROR);
	}

	@Override
	public String test(Feature feature, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			String s = (String) value;
			if (s.trim().length() == 0) {
				return null;
			}
			File f = new File(s);
			if (f.exists()) {
				return null;
			}
			
			return Messages.bind(Messages.FileExistsTester_FileNotExists, s);
		}
		
		return null;
	}

}
