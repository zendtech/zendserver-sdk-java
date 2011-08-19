package org.zend.php.zendserver.deployment.core.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.ReplaceEdit;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class DeploymentRenameParticipant extends RenameParticipant {

	private IResource renamedResource;

	public DeploymentRenameParticipant() {
	}

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IResource) {
			renamedResource = (IResource) element;
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
		
		IProject project = renamedResource.getProject();
		IDescriptorContainer container = DescriptorContainerManager.getService().openDescriptorContainer(project);
		if (! container.getFile().exists()) {
			return null;
		}
		
		RenameArguments args = getArguments();
		String newName = args.getNewName();
		
		IDeploymentDescriptor descriptor = container.getDescriptorModel();
		int origLength = container.getModelSerializer().getDocumentLength();
		IPath projectRelativePath = renamedResource.getProjectRelativePath();
		String oldFullPath = projectRelativePath.toString();
		String newFullPath = projectRelativePath.removeLastSegments(1).append(newName).toString();
		
		boolean hasChanged = updatePathInDescriptor(oldFullPath, newFullPath, descriptor);
		
		if (! hasChanged) {
			return null;
		}
		
		IDocument resultDocument = new Document();
		container.connect(resultDocument);
		container.save();
		
		TextFileChange change = new TextFileChange("rename", container.getFile());
		change.setEdit(new ReplaceEdit(0, origLength, resultDocument.get()));
		
		return change;
	}

	private boolean updatePathInDescriptor(String oldFullPath, String newFullPath,
			IDeploymentDescriptor descriptor) {
		boolean updated = false;
		
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.EULA, oldFullPath, newFullPath);
		updated |= updateIfEquals(descriptor, DeploymentDescriptorPackage.ICON, oldFullPath, newFullPath);
		
		return updated;
	}

	private boolean updateIfEquals(IModelObject object, Feature f, String oldValue, String newValue) {
		String path = object.get(f);
		if (oldValue.equals(path)) {
			object.set(f, newValue);
			return true;
		}
		
		return false;
	}

}
