package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
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
		return "Application Deployment";
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
		
		IDeploymentDescriptor descriptor = container.getDescriptorModel();
		
		DeploymentRefactoring r = new DeploymentRefactoring("rename"); //$NON-NLS-1$
		
		boolean hasChanged = false;
		if (container.getFile().exists()) {
			hasChanged = r.updatePathInDescriptor(oldFullPath, newFullPath, descriptor);
		}
		
		// TODO GET project descriptor from NEW PROJECT (after refactoring)
		if (affectedResource.getType() == IResource.PROJECT) {	
	//		hasChanged |= r.updateProjectName(newFullPath, descriptor);
		}
		
		if (! hasChanged) {
			return null;
		}
		
		TextFileChange change = r.createDescriptorTextChange(container);
		
		return change;
	}
}
