package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.ui.actions.IDragAndDropContribution;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;

@SuppressWarnings("restriction")
public class DeployProjectContribution implements IDragAndDropContribution {

	public void performAction(final Server server, final IProject project) {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return;
		}
		Job job = new Job("Deployment") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (!hasDeploymentNature(project)) {
						enableDeployment(project);
					}
					DeploymentHandler handler = new DeploymentHandler();
					IDeploymentHelper defaultHelper = LaunchUtils
							.createDefaultHelper(ServerUtils.getTarget(server)
									.getId(), project);
					if (handler.openNoConfigDeploymentWizard(defaultHelper,
							project) != IStatus.OK) {
						return Status.CANCEL_STATUS;
					}
				} catch (CoreException e) {
					Activator.log(e);
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	public boolean isAvailable(Server server) {
		return ServerUtils.getTarget(server) != null;
	}

	public boolean isSupported(Server server, IProject project) {
		return isAvailable(server) && hasPHPNature(project)
				&& !(hasDeploymentNature(project) && isLibrary(project));
	}

	private boolean hasDeploymentNature(IProject project) {
		try {
			return project.getNature(DeploymentNature.ID) != null;
		} catch (CoreException e) {
			Activator.log(e);

		}
		return false;
	}

	private boolean hasPHPNature(IProject project) {
		try {
			return project.getNature(PHPNature.ID) != null;
		} catch (CoreException e) {
			Activator.log(e);

		}
		return false;
	}

	private void enableDeployment(IProject project) throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
		String[] nnatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, nnatures, 0, natures.length);
		nnatures[natures.length] = DeploymentNature.ID;
		desc.setNatureIds(nnatures);
		project.setDescription(desc, new NullProgressMonitor());
	}

	private boolean isLibrary(IProject project) {
		IDescriptorContainer container = DescriptorContainerManager
				.getService().openDescriptorContainer(project);
		IDeploymentDescriptor desc = container.getDescriptorModel();
		return desc.getType() == ProjectType.LIBRARY;
	}

}
