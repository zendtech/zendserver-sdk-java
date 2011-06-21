package org.zend.php.zendserver.deployment.core.internal.validation;

import java.io.File;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class FileExistsTester extends PropertyTester {

	public FileExistsTester(Feature[] strings) {
		super(ValidationStatus.ERROR, strings);
	}

	@Override
	String test(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			File f = new File((String) value);
			if (f.exists()) {
				return null;
			}
		}
		
		return "File does not exist.";
	}

}
