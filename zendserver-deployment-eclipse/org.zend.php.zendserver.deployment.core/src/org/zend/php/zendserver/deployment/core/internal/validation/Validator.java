package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class Validator {

	private Map<Feature, PropertyTester[]> testers;
	
	public Validator() {
		testers = new HashMap<Feature, PropertyTester[]>();
		
		PropertyTester tester = new FieldNotEmptyTester();
		add(DeploymentDescriptorPackage.PKG_NAME, tester);
		add(DeploymentDescriptorPackage.VERSION_RELEASE, tester);
		add(DeploymentDescriptorPackage.APPDIR, tester); // may be empty but must exist
		
		add(DeploymentDescriptorPackage.ID, tester);
		add(DeploymentDescriptorPackage.DISPLAY, tester);
		add(DeploymentDescriptorPackage.TYPE, tester);
		
		add(DeploymentDescriptorPackage.DEPENDENCY_NAME, tester);
		
		add(DeploymentDescriptorPackage.VAR_NAME, tester);
		add(DeploymentDescriptorPackage.VALUE, tester);
		
		tester = new FileExistsTester();
		add(DeploymentDescriptorPackage.APPDIR, tester);
		add(DeploymentDescriptorPackage.ICON, tester);
		add(DeploymentDescriptorPackage.EULA, tester);
		add(DeploymentDescriptorPackage.SCRIPTSDIR, tester);
		add(DeploymentDescriptorPackage.DOCROOT, tester);
		
		tester = new VersionTester();
		add(DeploymentDescriptorPackage.VERSION_API, tester);
		add(DeploymentDescriptorPackage.VERSION_RELEASE, tester);
	}
	
	protected void add(Feature feature, PropertyTester tester) {
		PropertyTester[] now = testers.get(feature);
		if (now == null) {
			testers.put(feature, new PropertyTester[] {tester});
		} else {
			PropertyTester[] dest = new PropertyTester[now.length + 1];
			System.arraycopy(now, 0, dest,0, now.length);
			dest[now.length] = tester;
			testers.put(feature, dest);
		}
	}
	
	public void validate(IDeploymentDescriptor descr, List<ValidationStatus> statuses) {
		// empty
	}
	
	public List<ValidationStatus> validate(IDeploymentDescriptor descr) {
		List<ValidationStatus> statuses = new ArrayList<ValidationStatus>();
		
		for (Entry<Feature, PropertyTester[]> v : testers.entrySet()) {
			Object value = descr.get(v.getKey());
			for (PropertyTester pt : v.getValue()) {
				String message = pt.test(value);
				
				if (message != null) {
					statuses.add(new ValidationStatus(v.getKey(), pt.severity, message));
				}
			}
		}
		
		return statuses;
	}
	
}
