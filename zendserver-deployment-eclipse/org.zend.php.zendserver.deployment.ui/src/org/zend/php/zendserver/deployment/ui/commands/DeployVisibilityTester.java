package org.zend.php.zendserver.deployment.ui.commands;


import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;


public class DeployVisibilityTester extends PropertyTester {

	private static final String PHP_NATURE = "org.eclipse.php.core.PHPNature"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) receiver;
			Object adapter = (IResource) adaptable.getAdapter(IResource.class);
			if (adapter != null) {
				receiver = adapter;
			}
		}
		
		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;
			
			try {
				Object phpNature = project.getNature(PHP_NATURE);
				if (phpNature != null) {
					return true;
				}
			} catch (CoreException e) {
				// Ignore this exception
			} 
		}
		
		
		if (receiver instanceof IFile) {
			IFile file = (IFile) receiver;
			if (DescriptorContainerManager.DESCRIPTOR_PATH.equals(file.getName())) {
				return true;
			}
		}
		return false;
	}

}
