package org.zend.php.zendserver.deployment.core.internal.descriptor;

import junit.framework.TestCase;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.descriptor.IZendComponentDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;

public class DescriptorNotificationsTests extends TestCase {

	public static class TestEvent implements IDescriptorChangeListener {

		private int eventNo;
		private IModelObject expectedTarget;
		private Feature expectedFeature;
		private int expectedType;
		
		private int eventCounter;
		public boolean eventFound;
		
		public static TestEvent assertEvent(int eventNo, IModelContainer expectedTarget, Feature expectedFeature, int expectedType) {
			TestEvent event = new TestEvent(eventNo, expectedTarget, expectedFeature, expectedType);
			expectedTarget.addListener(event);
			
			return event;
		}
		
		private TestEvent(int eventNo, IModelObject expectedTarget, Feature expectedFeature, int expectedType) {
			this.eventNo = eventNo;
			this.expectedTarget = expectedTarget;
			this.expectedFeature = expectedFeature;
			this.expectedType = expectedType;
			
		}
			
		public void descriptorChanged(IModelObject target, Feature feature, int type) {
			if (eventCounter == eventNo) {
				assertSame(expectedTarget, target);
				assertSame(expectedFeature, feature);
				assertEquals(expectedType, type);
				this.eventFound = true;
			}
			eventCounter++;	
		}
	}
	
	public void testDescriptorSet() {
		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		TestEvent ev1 = TestEvent.assertEvent(0, descr, IDeploymentDescriptor.NAME, IDescriptorChangeListener.SET);
		TestEvent.assertEvent(1, descr, IDeploymentDescriptor.VERSION_API, IDescriptorChangeListener.SET);
		TestEvent.assertEvent(2, descr, IDeploymentDescriptor.SUMMARY, IDescriptorChangeListener.SET);
		
		descr.setName("newName");
		descr.setApiVersion("1.2.0");
		descr.set(IDeploymentDescriptor.SUMMARY, "new summary");
		
		assertEquals(3, ev1.eventCounter);
	}
	
	public void testDescriptorAdd() {
		DeploymentDescriptor descr = new DeploymentDescriptor();
		
		TestEvent ev1 = TestEvent.assertEvent(0, descr, IDeploymentDescriptor.VARIABLES, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(1, descr, IDeploymentDescriptor.DEPENDENCIES_PHP, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(2, descr, IDeploymentDescriptor.PARAMETERS, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(3, descr, IDeploymentDescriptor.PERSISTENT_RESOURCES, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(4, descr, IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(5, descr, IDeploymentDescriptor.DEPENDENCIES_EXTENSION, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(6, descr, IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(7, descr, IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT, IDescriptorChangeListener.ADD);
		TestEvent.assertEvent(8, descr, IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER, IDescriptorChangeListener.ADD);
		
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.VARIABLES);
		descr.getVariables().add(var);
		
		IPHPDependency php = (IPHPDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_PHP);
		descr.getPHPDependencies().add(php);
		
		IParameter param = (IParameter) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.PARAMETERS);
		descr.getParameters().add(param);
		
		descr.getPersistentResources().add("c:\\Program Files");
		
		IDirectiveDependency dd = (IDirectiveDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE);
		descr.getDirectiveDependencies().add(dd);
		
		IExtensionDependency ext = (IExtensionDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_EXTENSION);
		descr.getExtensionDependencies().add(ext);
		
		IZendFrameworkDependency zf = (IZendFrameworkDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK);
		descr.getZendFrameworkDependencies().add(zf);
		
		IZendComponentDependency zc = (IZendComponentDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT);
		descr.getZendComponentDependencies().add(zc);
		
		IZendServerDependency zs = (IZendServerDependency) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER);
		descr.getZendServerDependencies().add(zs);
		
		
		assertEquals(9, ev1.eventCounter);
	}
}
