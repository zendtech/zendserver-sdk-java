package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Messages;


public class DependenciesMasterDetailsProvider implements MasterDetailsProvider {
	
	public String getDescription() {
		return Messages.DependenciesMasterDetailsProvider_Description;
	}
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
			List all = new ArrayList();
			all.addAll(descr.getPHPDependencies());
			all.addAll(descr.getDirectiveDependencies());
			all.addAll(descr.getExtensionDependencies());
			all.addAll(descr.getZendFrameworkDependencies());
			all.addAll(descr.getZendServerDependencies());
			all.addAll(descr.getZendComponentDependencies());
			return all.toArray();
		}
		
		if (input instanceof Object[]) {
			return (Object[])input;
		}
		
		return null;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		Object[] input = new Object[] {
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP),
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE),
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION),
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK),
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER),
			DeploymentDescriptorFactory.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT),
		};
		
		ListDialog sd = new ListDialog(block.viewer.getControl().getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage(Messages.DependenciesMasterDetailsProvider_DependencyType);
		sd.setTitle(Messages.DependenciesMasterDetailsProvider_Add);
		
		if (sd.open() == Window.CANCEL) {
			return null;
		}
		
		return sd.getResult()[0];
	}
}
