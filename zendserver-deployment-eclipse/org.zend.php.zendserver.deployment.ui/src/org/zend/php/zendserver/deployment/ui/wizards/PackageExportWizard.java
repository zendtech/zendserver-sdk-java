package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.application.PackageBuilder;

public class PackageExportWizard extends Wizard implements IExportWizard {

	private PackageExportPage parametersPage;
	
	public PackageExportWizard() {
		parametersPage = new PackageExportPage();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(false);
		setWindowTitle("Export Deployment Package");
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_DEPLOY_WIZARD));
	}
	
	public void setProject(IProject project) {
		parametersPage.setSelection(project);
	}

	@Override
	public void addPages() {
		addPage(parametersPage);
	}

	@Override
	public boolean performFinish() {
		final IResource[] projects = parametersPage.getSelectedProjects();
		final File directory = new File(parametersPage.getDestinationValue());
		Job createPackageJob = new Job("Create Deployment Package(s)...") {
			private StatusChangeListener listener;

			public IStatus run(IProgressMonitor monitor) {
				listener = new StatusChangeListener(monitor);
				if (monitor.isCanceled()) {
					return Status.OK_STATUS;
				}
				for (IResource project : projects) {
					File container = new File(project.getLocation().toOSString());
					PackageBuilder builder = new PackageBuilder(container,
							new EclipseMappingModelLoader());
					builder.addStatusChangeListener(listener);
					builder.createDeploymentPackage(directory);
					if (monitor.isCanceled()) {
						return Status.OK_STATUS;
					}
				}
				return new SdkStatus(listener.getStatus());
			}
		};
		createPackageJob.setUser(true);
		createPackageJob.schedule();
		return true;
	}

}
