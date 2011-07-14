package org.zend.php.zendserver.deployment.core.internal.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class ValidatorSemanticValidator {

	private Map<Feature, PropertyTester[]> testers;
	
	public ValidatorSemanticValidator() {
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
	
	private void validate(IModelObject modelObj, List<ValidationStatus> statuses) {
		validateProperties(modelObj, statuses);
		if (modelObj instanceof IModelContainer) {
			validate((IModelContainer) modelObj, statuses);
		}
	}
	
	private void validate(IModelContainer obj, List<ValidationStatus> statuses) {
		for (Feature f : obj.getChildNames()) {
			PropertyTester[] featureTests = testers.get(f);
			if (featureTests != null) {
				List<Object> children = obj.getChildren(f);
				for (PropertyTester pt : featureTests) {
					String msg = pt.test(children);
					if (msg != null) {
						statuses.add(new ValidationStatus(f, obj.getLine(f), obj.getChar(f), obj.getLength(f), pt.severity, msg));
					}
				}
			}
			
			if (f.type == IModelObject.class) {
				List<Object> children = obj.getChildren(f);
				for (Object child : children) {
					validate((IModelObject) child, statuses);
				}
			}
		}
	}
	
	private void validateProperties(IModelObject obj, List<ValidationStatus> statuses) {
		for (Feature f : obj.getPropertyNames()) {
			PropertyTester[] featureTests = testers.get(f);
			if (featureTests != null) {
				Object value = obj.get(f);
				for (PropertyTester pt: featureTests) {
					String msg = pt.test(value);
					if (msg != null) {
						statuses.add(new ValidationStatus(f, obj.getLine(f), obj.getChar(f), obj.getLength(f), pt.severity, msg));
					}
				}
			}
		}
	}
	
	public ValidationStatus[] validate(IModelObject descr) {
		List<ValidationStatus> statuses = new ArrayList<ValidationStatus>();
		validate(descr, statuses);
		return statuses.toArray(new ValidationStatus[statuses.size()]);
	}
	
}
