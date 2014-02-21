package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;

public class DescriptorRenameParticipant extends RenameParticipant {

	private IResource affectedResource;

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IResource) {
			affectedResource = (IResource) element;
		}
		return true;
	}

	@Override
	public String getName() {
		return Messages.Application_Deployment;
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		RenameArguments args = getArguments();
		String newName = args.getNewName();
		
		IPath projectRelativePath = affectedResource.getProjectRelativePath();
		String oldFullPath = projectRelativePath.toString();
		String newFullPath = projectRelativePath.removeLastSegments(1).append(newName).toString();
				
		IProject project = affectedResource.getProject();
		
		IDescriptorContainer container = DescriptorContainerManager.getService().openDescriptorContainer(project);
		IFile fileToChange = container.getFile();
		
		if (! fileToChange.exists()) {
			return null;
		}
		
		if (affectedResource.getType() == IResource.PROJECT) { // project rename - get the new project
			fileToChange = ResourcesPlugin.getWorkspace().getRoot().getProject(newFullPath).getFile(DescriptorContainerManager.DESCRIPTOR_PATH);
		}
		
		IDeploymentDescriptor descriptor = container.getDescriptorModel();
		
		DeploymentRefactoring r = new DeploymentRefactoring("rename"); //$NON-NLS-1$
		
		boolean hasChanged = false;
		if (container.getFile().exists()) {
			hasChanged = r.updatePathInDescriptor(oldFullPath, newFullPath, descriptor);
		}
		
		// on project rename do to update project name in descriptor
		if (affectedResource.getType() == IResource.PROJECT) {	
			hasChanged |= r.updateProjectName(affectedResource.getName(), newFullPath, descriptor);
		}
		
		if (! hasChanged) {
			return null;
		}
		
		Change change = r.createDescriptorPHPTextChange(container.getFile(),fileToChange, container);
//		Change change = r.createDescriptorTextChange(fileToChange, container);
		
		return change;
	}
}
