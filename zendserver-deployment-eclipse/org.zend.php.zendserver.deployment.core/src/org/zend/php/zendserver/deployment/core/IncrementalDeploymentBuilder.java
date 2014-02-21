package org.zend.php.zendserver.deployment.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorValidator;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.sdklib.mapping.MappingModelFactory;
import org.zend.sdklib.mapping.validator.IMappingValidator;
import org.zend.sdklib.mapping.validator.MappingParseException;
import org.zend.sdklib.mapping.validator.MappingParseStatus;
import org.zend.sdklib.mapping.validator.MappingValidator;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	public static final String PROBLEM_MARKER = "org.zend.php.zendserver.deployment.core.problemmarker"; //$NON-NLS-1$
	public static final String ID = DeploymentCore.PLUGIN_ID + ".DeploymentBuilder"; //$NON-NLS-1$
	public static final String FEATURE_ID = "feature.id"; //$NON-NLS-1$
	public static final String OBJECT_ID = "object.id"; //$NON-NLS-1$
	public static final String OBJECT_NUMBER = "object.no"; //$NON-NLS-1$

	private DescriptorValidator descrValidator = new DescriptorValidator();
	
	@Override
	protected IProject[] build(int kind, Map args,
			IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			return null;
		}
		
		// find all projects touched in change
		final Set<IProject> projects = new HashSet<IProject>();
		delta.accept(new IResourceDeltaVisitor() {
			
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource resource = delta.getResource();
				IProject project = resource.getProject();
				projects.add(project);
				return true;
			}
		});
		
		// for each touched project validate descriptor
		for (IProject project : projects) {
			IFile descrFile = project.getFile(DescriptorContainerManager.DESCRIPTOR_PATH);
			validateDescriptor(descrFile);
			
			IFile mappingFile = project.getFile(MappingModelFactory.DEPLOYMENT_PROPERTIES);
			validateMapping(mappingFile);
		}
		
		return null;
	}
	
	private void validateDescriptor(IFile file) {
		descrValidator.validate(file);
	}

	protected void validateMapping(IFile file) {
		IMappingValidator validator = new MappingValidator(file.getParent().getLocation().toFile());
		validator.setVariableResolver(new EclipseVariableResolver());
		try {
			try {
				if (file.exists()) {
					file.refreshLocal(IResource.DEPTH_ONE,
							new NullProgressMonitor());
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
