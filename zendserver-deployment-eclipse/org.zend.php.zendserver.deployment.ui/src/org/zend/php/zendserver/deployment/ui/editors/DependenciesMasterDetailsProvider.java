package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.ui.Messages;

public class DependenciesMasterDetailsProvider implements MasterDetailsProvider {

	private IDescriptorContainer fModel;
	
	public DependenciesMasterDetailsProvider(IDescriptorContainer model) {
		this.fModel = model;
	}

	public String getDescription() {
		if (fModel.getDescriptorModel().getType() == ProjectType.LIBRARY) {
			return Messages.DependenciesMasterDetailsProvider_DescriptionLibrary;
		}
		return Messages.DependenciesMasterDetailsProvider_Description;
	}

	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
			List<IModelObject> all = new ArrayList<IModelObject>();
			all.addAll(descr.getPHPDependencies());
			all.addAll(descr.getExtensionDependencies());
			all.addAll(descr.getDirectiveDependencies());
			all.addAll(descr.getZendServerDependencies());
			all.addAll(descr.getZendComponentDependencies());
			all.addAll(descr.getZendFrameworkDependencies());
			all.addAll(descr.getZendFramework2Dependencies());
			all.addAll(descr.getPHPLibraryDependencies());
			return all.toArray();
		}

		if (input instanceof Object[]) {
			return (Object[]) input;
		}

		return null;
	}

	public Object addElment(IDeploymentDescriptor model,
			DescriptorMasterDetailsBlock block) {
		Object[] input = new Object[] {
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_PHP),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_EXTENSION),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_DIRECTIVE),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDSERVER),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZSCOMPONENT),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK),
				DeploymentDescriptorFactory
						.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_ZENDFRAMEWORK2) };

		final Control control = block.viewer.getControl();
		final ListDialog sd = new ListDialog(control.getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer
				.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage(Messages.DependenciesMasterDetailsProvider_DependencyType);
		sd.setTitle(Messages.DependenciesMasterDetailsProvider_Add);

		if (sd.open() == Window.CANCEL) {
			return null;
		}

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
