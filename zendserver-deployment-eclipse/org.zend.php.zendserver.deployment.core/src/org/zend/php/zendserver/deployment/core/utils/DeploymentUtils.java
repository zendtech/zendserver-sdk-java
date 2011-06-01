package org.zend.php.zendserver.deployment.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;


public class DeploymentUtils {

	public static IDeploymentDescriptor createDescriptor(IProject project,
			String name, String folder, IProgressMonitor monitor) {

		IDescriptorContainer container = DescriptorContainerManager
				.getService().openDescriptorContainer(project);

		IDeploymentDescriptorModifier mo = container.createWorkingCopy();
		try {
			mo.setName(name);
			mo.setDocumentRoot(folder);
			mo.save();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return container.getDescriptorModel();

	}

}
