package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.descriptor.IZendComponentDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;

public class DescriptorNotificationsTests extends TestCase {
	
	private EventRecorder events;
	private DeploymentDescriptor descr;
	
	public static class EventRecorder implements IDescriptorChangeListener {

		private List<ChangeEvent> expected = new ArrayList<ChangeEvent>();
		private List<ChangeEvent> actual = new ArrayList<ChangeEvent>(); 
		
		public void assertEvent(IModelObject expectedTarget, Feature expectedFeature, int expectedType, Object newValue, Object oldValue) {
			expected.add(new ChangeEvent(expectedTarget, expectedFeature, expectedType, newValue, oldValue));
		}
			
		public void descriptorChanged(ChangeEvent event) {
			actual.add(event);
		}
		
		public void assertEquals() {
			assertSame(expected.size(), actual.size());
			for (int i = 0; i < expected.size(); i++) {
				ChangeEvent e = expected.get(i);
				ChangeEvent a = actual.get(i);
				assertSame("Event target should be same for event "+i, e.target, a.target);
				assertSame("Event feature should be same for event "+i, e.feature, a.feature);
				assertSame("Event type should be same for event "+i, e.type, a.type);
				TestCase.assertEquals("Event newValue should be equal for event "+i, e.newValue, a.newValue);
				TestCase.assertEquals("Event oldValue should be equal for event "+i, e.oldValue, a.oldValue);
			}
		}
	}
	
	public void setUp() {
		events = new EventRecorder();
		descr = new DeploymentDescriptor();
		descr.addListener(events);
	}
	
	public void testDescriptorSet() {
		events.assertEvent(descr, DeploymentDescriptorPackage.PKG_NAME, IDescriptorChangeListener.SET, "newName", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.VERSION_API, IDescriptorChangeListener.SET, "1.2.0", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.SUMMARY, IDescriptorChangeListener.SET, "new summary", null);
		
		descr.setName("newName");
		descr.setApiVersion("1.2.0");
		descr.set(DeploymentDescriptorPackage.SUMMARY, "new summary");
		
		events.assertEquals();
	}
	
	public void testDescriptorAdd() {
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.VARIABLES);
		IPHPDependency php = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP);
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.PARAMETERS);
		IDirectiveDependency dd = (IDirectiveDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE);
		IExtensionDependency ext = (IExtensionDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION);
		IZendFrameworkDependency zf = (IZendFrameworkDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK);
		IZendComponentDependency zc = (IZendComponentDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT);
		IZendServerDependency zs = (IZendServerDependency) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER);
		
		events.assertEvent(descr, DeploymentDescriptorPackage.VARIABLES, IDescriptorChangeListener.ADD, var, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_PHP, IDescriptorChangeListener.ADD, php, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.PARAMETERS, IDescriptorChangeListener.ADD, param, null);
		events.assertEvent(param, DeploymentDescriptorPackage.VALIDATION, IDescriptorChangeListener.ADD, "value1", null);
		events.assertEvent(param, DeploymentDescriptorPackage.VALIDATION, IDescriptorChangeListener.ADD, "value2", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.PERSISTENT_RESOURCES, IDescriptorChangeListener.ADD, "c:\\Program Files", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE, IDescriptorChangeListener.ADD, dd, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION, IDescriptorChangeListener.ADD, ext, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK, IDescriptorChangeListener.ADD, zf, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT, IDescriptorChangeListener.ADD, zc, null);
		events.assertEvent(descr, DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER, IDescriptorChangeListener.ADD, zs, null);
		
		descr.getVariables().add(var);
		descr.getPHPDependencies().add(php);
		descr.getParameters().add(param);
		param.getValidValues().add("value1");
		param.getValidValues().add("value2");
		descr.getPersistentResources().add("c:\\Program Files");
		descr.getDirectiveDependencies().add(dd);
		descr.getExtensionDependencies().add(ext);
		descr.getZendFrameworkDependencies().add(zf);
		descr.getZendComponentDependencies().add(zc);
		descr.getZendServerDependencies().add(zs);
		
		
		events.assertEquals();
	}
	
	public void testDescriptorEventBubble() {
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.VARIABLES);
		
		events.assertEvent(descr, DeploymentDescriptorPackage.VARIABLES, IDescriptorChangeListener.ADD, var, null);
		events.assertEvent(var, DeploymentDescriptorPackage.VAR_NAME, IDescriptorChangeListener.SET, "changedName", "newName");

		var.setName("newName"); // no event for that in descr
		
		descr.getVariables().add(var); // event for that in descr
		
		var.setName("changedName"); // event for that in descr
		
		events.assertEquals();
	}
	
	public void testDescriptorNoEventOnNoChange() {
		events.assertEvent(descr, DeploymentDescriptorPackage.PKG_NAME, IDescriptorChangeListener.SET, "newName", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.PKG_NAME, IDescriptorChangeListener.SET, null, "newName");
		events.assertEvent(descr, DeploymentDescriptorPackage.PKG_NAME, IDescriptorChangeListener.SET, "Magento", null);
		events.assertEvent(descr, DeploymentDescriptorPackage.PKG_NAME, IDescriptorChangeListener.SET, "Magentissimo", "Magento");
		
		descr.setName("newName");
		descr.setName("newName");
		descr.setName("newName");
		descr.setName("newName");
		descr.setName(null);
		descr.setName(null);
		descr.setName(null);
		descr.setName("Magento");
		descr.setName("Magentissimo");
		
		events.assertEquals();
	}
}
