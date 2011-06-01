package org.zend.php.zendserver.deployment.ui.commands;


import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.core.project.PHPNature;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;


public class DeployVisibilityTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IScriptProject) {
			IScriptProject project = (IScriptProject) receiver;
			IProjectNature phpNature = null;
			try {
				phpNature = project.getProject()
						.getNature(PHPNature.ID);
				if (phpNature != null) {
					return true;
				}
			} catch (CoreException e) {
				// Ignore this exception
			} 
		} else if (receiver instanceof IFile) {
			IFile file = (IFile) receiver;
			if (DescriptorContainerManager.DESCRIPTOR_PATH.equals(file.getName())) {
				return true;
			}
		}
		return false;
	}

}
