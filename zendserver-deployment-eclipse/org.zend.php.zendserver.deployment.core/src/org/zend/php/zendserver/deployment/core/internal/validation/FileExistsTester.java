package org.zend.php.zendserver.deployment.core.internal.validation;

import java.io.File;

public class FileExistsTester extends PropertyTester {

	public FileExistsTester(String[] strings) {
		super(ValidationStatus.ERROR, strings);
	}

	@Override
	String test(Object property) {
		if ((property != null) && (property instanceof String)) {
			File f = new File((String) property);
			if (f.exists()) {
				return null;
			}
		}
		
		return "File does not exist.";
	}

}
