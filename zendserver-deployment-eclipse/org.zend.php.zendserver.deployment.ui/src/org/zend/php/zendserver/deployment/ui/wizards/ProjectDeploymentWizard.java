package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.core.utils.PackageBuilder;
import org.zend.php.zendserver.deployment.core.utils.WebApiManager;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;


public class ProjectDeploymentWizard extends Wizard {

	private DeploymentDescriptorPage descriptorPage;
	private ApplicationParametersPage parametersPage;
	private IDescriptorContainer model;

	public ProjectDeploymentWizard(IDescriptorContainer model) {
		if (!model.getFile().exists()) {
			descriptorPage = new DeploymentDescriptorPage(model);
		}
		this.model = model;
		parametersPage = new ApplicationParametersPage(model);
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.deployWizardTitle);
		setDefaultPageImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_DEPLOY_WIZARD));
	}

	@Override
	public void addPages() {
		super.addPages();
		if (!model.getFile().exists()) {
			addPage(descriptorPage);
		}
		addPage(parametersPage);
	}

	@Override
	public boolean performFinish() {
		final String name = descriptorPage != null ? descriptorPage
				.getApplciationName() : null;
		final String folder = descriptorPage != null ? descriptorPage
				.getDocumentRoot() : null;
		final String baseUrl = parametersPage.getBaseURL();
		final Server targetLocation = parametersPage.getTargetLocation();
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
				getShell());
		try {
			progressDialog.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					File zpkPackage = null;
					try {
						createDeploymentDescriptor(name, folder, monitor);
						PackageBuilder builder = new PackageBuilder(model);
						zpkPackage = builder.createDeploymentPackage(monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (zpkPackage != null) {
						deployApplication(zpkPackage, baseUrl,
								targetLocation, monitor);
					}

				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated method stub
		} catch (InterruptedException e) {
			// TODO Auto-generated method stub
		}
		return true;
	}

	protected void deployApplication(File zpkPackage, String baseUrl,
			Server targetLocation, IProgressMonitor monitor) {
		String host = targetLocation.getBaseURL();
		WebApiManager manager = new WebApiManager(host);
		monitor.beginTask("Deploying application to target location...", 1);
		if (manager.connect()) {
			ApplicationInfo info = manager.applicationDeploy(zpkPackage,
					baseUrl);
			monitor.done();
			ApplicationStatus status = info.getStatus();
			monitor.beginTask("Deploying application to target location ("
					+ status.getName() + ")...", 1);
			while (status == ApplicationStatus.STAGING) {
				status = manager.getApplicationStatus(info.getId());
			}
		}

	}

	@Override
	public boolean performCancel() {
		// TODO Auto-generated method stub
		return true;
	}

	private void createDeploymentDescriptor(final String name,
			final String folder, IProgressMonitor monitor) throws CoreException {
		IProject project = model.getFile().getProject();
		if (!model.getFile().exists()) {
			monitor.beginTask("Creating deployment descriptor...", 1);
			DeploymentUtils.createDescriptor(project, name, folder, monitor);
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}
			monitor.done();
			project.refreshLocal(IResource.DEPTH_ONE, monitor);
			if (monitor.isCanceled()) {
				return;
			}
		}
	}
}
