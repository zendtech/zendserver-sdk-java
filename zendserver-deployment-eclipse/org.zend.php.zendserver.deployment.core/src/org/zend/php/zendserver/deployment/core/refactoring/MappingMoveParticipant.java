package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.core.resources.IContainer;
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
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMappingModel;

public class MappingMoveParticipant extends MoveParticipant {
	
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
		MoveArguments args = getArguments();
		Object newPath = args.getDestination();
		if (! (newPath instanceof IContainer)) {
			return null;
		}
		
		IContainer newParent = (IContainer) newPath;
		IPath projectRelativePath = affectedResource.getProjectRelativePath();
		String oldFullPath = projectRelativePath.toString();
		String newFullPath = newParent.getProjectRelativePath().append(affectedResource.getName()).toString();
		
		IProject project = affectedResource.getProject();
		IDescriptorContainer container = DescriptorContainerManager.getService().openDescriptorContainer(project);
		container.initializeMappingModel(null);
		IMappingModel mapping = container.getMappingModel();
		if (! mapping.getMappingFile().exists()) {
			return null;
		}
		
		DeploymentRefactoring r = new DeploymentRefactoring("move");
		
		boolean hasChanged;
		if (newParent.getProject().equals(affectedResource.getProject())) {
			hasChanged = r.updatePathInMapping(oldFullPath, newFullPath, mapping);
		} else {
			// move from one project to another - make no changes, leave it for validator to detect error
			hasChanged = r.removePathFromMapping(oldFullPath, mapping);
		}
		
		
		if (! hasChanged) {
			return null;
		}
		
		TextFileChange change = r.createMappingTextChange(container);
		
		return change;
	}

}
