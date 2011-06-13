package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;

public class Validator {

	private List<PropertyTester> testers;
	
	public Validator() {
		testers = new ArrayList<PropertyTester>();
		testers.add(new FieldNotEmptyTester(new String[] {
				IDeploymentDescriptor.NAME,
				IDeploymentDescriptor.VERSION_RELEASE,
				IDeploymentDescriptor.APPDIR,
		}));
		
		testers.add(new FileExistsTester(new String[] {
			IDeploymentDescriptor.APPDIR,
			IDeploymentDescriptor.ICON,
			IDeploymentDescriptor.EULA,
			IDeploymentDescriptor.SCRIPTSDIR,
			IDeploymentDescriptor.DOCROOT
		}));
	}
	
	public void validate(IDeploymentDescriptor descr, List<ValidationStatus> statuses) {
		// empty
	}
	
	public List<ValidationStatus> validate(IDeploymentDescriptor descr) {
		List<ValidationStatus> statuses = new ArrayList<ValidationStatus>();
		
		for (PropertyTester v : testers) {
			v.validate(descr, statuses);
		}
		
		return statuses;
	}
	
}
