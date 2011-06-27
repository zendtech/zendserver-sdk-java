package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
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

	public static class Event {
		IModelObject target;
		Feature feature;
		int type;
		
		public Event(IModelObject target, Feature feature, int type) {
			this.target = target;
			this.feature = feature;
			this.type = type;
		}
	}
	
	public static class EventRecorder implements IDescriptorChangeListener {

		private List<Event> expected = new ArrayList<Event>();
		private List<Event> actual = new ArrayList<Event>(); 
		
		public void assertEvent(IModelObject expectedTarget, Feature expectedFeature, int expectedType) {
			expected.add(new Event(expectedTarget, expectedFeature, expectedType));
		}
			
		public void descriptorChanged(ChangeEvent event) {
			actual.add(new Event(event.target, event.feature, event.type));
		}
		
		public void assertEquals() {
			assertSame(expected.size(), actual.size());
			for (int i = 0; i < expected.size(); i++) {
				Event e = expected.get(i);
				Event a = actual.get(i);
				assertSame("Event target should be same for event "+i, e.target, a.target);
				assertSame("Event feature should be same for event "+i, e.feature, a.feature);
				assertSame("Event type should be same for event "+i, e.type, a.type);
			}
		}
	}
	
	public void setUp() {
		events = new EventRecorder();
		descr = new DeploymentDescriptor();
		descr.addListener(events);
	}
	
	public void testDescriptorSet() {
		events.assertEvent(descr, IDeploymentDescriptor.NAME, IDescriptorChangeListener.SET);
		events.assertEvent(descr, IDeploymentDescriptor.VERSION_API, IDescriptorChangeListener.SET);
		events.assertEvent(descr, IDeploymentDescriptor.SUMMARY, IDescriptorChangeListener.SET);
		
		descr.setName("newName");
		descr.setApiVersion("1.2.0");
		descr.set(IDeploymentDescriptor.SUMMARY, "new summary");
		
		events.assertEquals();
	}
	
	public void testDescriptorAdd() {
		events.assertEvent(descr, IDeploymentDescriptor.VARIABLES, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_PHP, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.PARAMETERS, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.PERSISTENT_RESOURCES, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_EXTENSION, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT, IDescriptorChangeListener.ADD);
		events.assertEvent(descr, IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER, IDescriptorChangeListener.ADD);
		
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
		
		
		events.assertEquals();
	}
	
	public void testDescriptorEventBubble() {
		events.assertEvent(descr, IDeploymentDescriptor.VARIABLES, IDescriptorChangeListener.ADD);
		
		IVariable var = (IVariable) DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.VARIABLES);
		events.assertEvent(var, IVariable.NAME, IDescriptorChangeListener.SET);

		var.setName("newName"); // no event for that in descr
		
		descr.getVariables().add(var); // event for that in descr
		
		var.setName("changedName"); // event for that in descr
		
		events.assertEquals();
	}
}
