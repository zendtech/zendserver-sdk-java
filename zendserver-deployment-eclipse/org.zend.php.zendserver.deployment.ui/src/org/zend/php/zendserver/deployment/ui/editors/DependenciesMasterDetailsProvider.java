package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;


public class DependenciesMasterDetailsProvider implements MasterDetailsProvider {
	
	public String getDescription() {
		return "Following will be required in order to install application.";
	}
	
	public Object[] doGetElements(Object input) {
		IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
		List all = new ArrayList();
		all.addAll(descr.getPHPDependencies());
		all.addAll(descr.getDirectiveDependencies());
		all.addAll(descr.getExtensionDependencies());
		all.addAll(descr.getZendFrameworkDependencies());
		all.addAll(descr.getZendServerDependencies());
		all.addAll(descr.getZendComponentDependencies());
		
		if (input instanceof IDeploymentDescriptor) {
			return all.toArray();
		}
		
		if (input instanceof Object[]) {
			return (Object[])input;
		}
		
		return null;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		Object[] input = new Object[] {
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_PHP),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_EXTENSION),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT),
		};
		
		ListDialog sd = new ListDialog(block.viewer.getControl().getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage("Dependency Type:");
		sd.setTitle("Add Dependency");
		
		if (sd.open() == Window.CANCEL) {
			return null;
		}
		
		return sd.getResult()[0];
	}
}
