package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class Validator {

	private List<PropertyTester> testers;
	
	public Validator() {
		testers = new ArrayList<PropertyTester>();
		testers.add(new FieldNotEmptyTester(new Feature[] {
				DeploymentDescriptorPackage.PKG_NAME,
				DeploymentDescriptorPackage.VERSION_RELEASE,
				DeploymentDescriptorPackage.APPDIR,
		}));
		
		testers.add(new FileExistsTester(new Feature[] {
				DeploymentDescriptorPackage.APPDIR,
				DeploymentDescriptorPackage.ICON,
				DeploymentDescriptorPackage.EULA,
				DeploymentDescriptorPackage.SCRIPTSDIR,
				DeploymentDescriptorPackage.DOCROOT
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
