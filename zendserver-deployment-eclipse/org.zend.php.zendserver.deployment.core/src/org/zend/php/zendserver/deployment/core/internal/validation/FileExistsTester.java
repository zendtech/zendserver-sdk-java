package org.zend.php.zendserver.deployment.core.internal.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class FileExistsTester extends PropertyTester {

	private DescriptorSemanticValidator validator;

	public FileExistsTester(DescriptorSemanticValidator validator, int severity) {
		super(severity);
		this.validator = validator;
	}

	@Override
	public String test(Feature feature, Object value, IModelObject object) {
		if (value == null) {
			return null;
		}
		
		IFile descriptorFile = validator.getFile();
		if (descriptorFile == null) {
			return null;
		}
		
		if (value instanceof String) {
			String s = (String) value;
			if (s.trim().length() == 0) {
				return null;
			}
			
			IFile f = descriptorFile.getParent().getFile(new Path(s));
			if (f.exists()) {
				return null;
			}
			
			return Messages.bind(Messages.FileExistsTester_FileNotExists, s);
		}
		
		return null;
	}

}
