package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;


public class DependenciesBlock extends DescriptorMasterDetailsBlock {
	
	public DependenciesBlock(DeploymentDescriptorEditor editor) {
		super(editor, "Dependencies", "Following will be required in order to install application.");
	}
	
	protected Object[] doGetElements(Object input) {
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
	
	protected void addElment() {
		Object[] input = new Object[] {
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_PHP),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_EXTENSION),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT),
		};
		
		ListDialog sd = new ListDialog(sashForm.getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage("Dependency Type:");
		sd.setTitle("Add Dependency");
		
		if (sd.open() == Window.CANCEL) {
			return;
		}
		
		Object result = sd.getResult()[0];
		Feature feature = DeploymentDescriptorFactory.getFeature(result);
		
		editor.getModel().add(feature, result);
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(result));
	}
}
