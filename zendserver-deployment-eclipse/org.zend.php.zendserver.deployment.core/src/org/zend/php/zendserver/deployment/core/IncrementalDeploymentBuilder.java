package org.zend.php.zendserver.deployment.core;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.core.internal.validation.Validator;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	public static final String PROBLEM_MARKER = "org.zend.php.zendserver.deployment.core.problemmarker"; //$NON-NLS-1$
	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentBuilder"; //$NON-NLS-1$

	public IncrementalDeploymentBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			return null;
		}
		
		delta.accept(new IResourceDeltaVisitor() {
			
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				if ((resource instanceof IFile) && (DescriptorContainerManager.DESCRIPTOR_PATH.equals(resource.getName()))) {
					validateDescriptor((IFile)resource);
				}
				
				if (resource instanceof IProject) {
					return true;
				}
				
				if (resource instanceof IFolder) {
					return false;
				}
				return false;
			}
		});
		
		return null;
	}

	protected void validateDescriptor(IFile file) {
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer(file);
		
		Validator validator = new Validator();
		ValidationStatus[] statuses = validator.validate(model.getDescriptorModel());
		
		try {
			file.deleteMarkers(PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
		
		for (ValidationStatus status : statuses) {
			IMarker marker;
			try {
				marker = file.createMarker(PROBLEM_MARKER);
				marker.setAttribute(IMarker.LINE_NUMBER, status.getLine());
				marker.setAttribute(IMarker.CHAR_START, status.getStart());
				marker.setAttribute(IMarker.CHAR_END, status.getEnd());
				marker.setAttribute(IMarker.MESSAGE, status.getMessage());
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
				marker.setAttribute(IMarker.SEVERITY, status.getSeverity());
			} catch (CoreException e) {
				DeploymentCore.log(e);
			}
			
		}
	}

}
