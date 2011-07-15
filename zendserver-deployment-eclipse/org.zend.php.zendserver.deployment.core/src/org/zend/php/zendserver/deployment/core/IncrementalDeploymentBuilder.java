package org.zend.php.zendserver.deployment.core;

import java.util.List;
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
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorValidator;
import org.zend.sdklib.mapping.MappingModelFactory;
import org.zend.sdklib.mapping.validator.IMappingValidator;
import org.zend.sdklib.mapping.validator.MappingParseException;
import org.zend.sdklib.mapping.validator.MappingParseStatus;
import org.zend.sdklib.mapping.validator.MappingValidator;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	public static final String PROBLEM_MARKER = "org.zend.php.zendserver.deployment.core.problemmarker"; //$NON-NLS-1$
	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentBuilder"; //$NON-NLS-1$

	private DescriptorValidator descrValidator = new DescriptorValidator();
	
	public IncrementalDeploymentBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map args,
			IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			return null;
		}
		
		delta.accept(new IResourceDeltaVisitor() {
			
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				if ((resource instanceof IFile) && (DescriptorContainerManager.DESCRIPTOR_PATH.equals(resource.getName()))) {
					extracted((IFile) resource);
				}
				
				if ((resource instanceof IFile)
						&& (MappingModelFactory.DEPLOYMENT_PROPERTIES.equals(resource.getName()))) {
					validateMapping((IFile) resource);
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
	
	private void extracted(IFile file) {
		try {
			file.deleteMarkers(PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
		
		descrValidator.validate(file);
	}

	protected void validateMapping(IFile file) {
		IMappingValidator validator = new MappingValidator(file.getParent().getLocation().toFile());
		try {
			try {
				if (file.exists()) {
					validator.parse(file.getContents());
					file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				}
			} catch (MappingParseException e) {
				handleMappingValidationErrors(file, e.getErrors());
			}
		} catch (CoreException e) {
			DeploymentCore.log(e);
		}
	}

	protected void handleMappingValidationErrors(IFile file, List<MappingParseStatus> errors)
			throws CoreException {
		file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		for (MappingParseStatus status : errors) {
			IMarker marker = file.createMarker(PROBLEM_MARKER);
			marker.setAttribute(IMarker.LINE_NUMBER, status.getLine());
			marker.setAttribute(IMarker.CHAR_START, status.getStart());
			marker.setAttribute(IMarker.CHAR_END, status.getEnd());
			marker.setAttribute(IMarker.MESSAGE, status.getMessage());
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		}
	}
}
