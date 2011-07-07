package org.zend.php.zendserver.deployment.core;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.validation.Validator;

public class IncrementalDeploymentBuilder extends IncrementalProjectBuilder {

	public static final String ID = DeploymentCore.PLUGIN_ID
			+ ".DeploymentBuilder"; //$NON-NLS-1$

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
				if ((resource instanceof IFile)
						&& (DescriptorContainerManager.DESCRIPTOR_PATH
								.equals(resource.getName()))) {
					validateDescriptor((IFile) resource);
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
		try {
			IDescriptorContainer model = DescriptorContainerManager
					.getService().openDescriptorContainer(file);

			Validator validator = new Validator();
			validator.validate(model.getDescriptorModel());
			// TODO Add markers

		} catch (Exception e) {
			// log any exception but please don't prompt for any error
			DeploymentCore.log(e);
		}
	}

}
