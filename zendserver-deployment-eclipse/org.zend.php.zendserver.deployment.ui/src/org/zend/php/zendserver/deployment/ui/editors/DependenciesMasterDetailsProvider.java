package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.wizards.NewDependencyWizard;


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
		
		final Control control = block.viewer.getControl();
		final ListDialog sd = new ListDialog(control.getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage(Messages.DependenciesMasterDetailsProvider_DependencyType);
		sd.setTitle(Messages.DependenciesMasterDetailsProvider_Add);
		
		if (sd.open() == Window.CANCEL) {
			return null;
		}
		
		BusyIndicator.showWhile(control.getDisplay() , new Runnable() {
			public void run() {
				NewDependencyWizard wizard = new NewDependencyWizard(sd.getResult()[0]);
				WizardDialog dialog = new WizardDialog(control.getShell(), wizard);
				dialog.create();
				// SWTUtil.setDialogSize(dialog, 400, 450);
				dialog.open();
			}
		});
		
		return sd.getResult()[0];
	}

	public Class getType() {
		return null;
	}

	public Object doGetParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}
