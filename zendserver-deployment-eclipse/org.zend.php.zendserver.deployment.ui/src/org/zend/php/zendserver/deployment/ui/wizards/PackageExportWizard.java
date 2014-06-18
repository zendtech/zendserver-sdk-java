package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.EclipseVariableResolver;
import org.zend.php.zendserver.deployment.core.sdk.ProductionPackageBuilder;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.application.PackageBuilder;
import org.zend.sdklib.mapping.IMappingLoader;

public class PackageExportWizard extends Wizard implements IExportWizard {

	private final class OverwriteRunnable implements Runnable {
		private final File p;
		private IStatus result;

		private OverwriteRunnable(File p) {
			this.p = p;
		}

		public void run() {
			final String message = MessageFormat
					.format(Messages.PackageExportWizard_0,
							p.getAbsolutePath());
			final boolean overwrite = MessageDialog.openQuestion(getShell(),
					Messages.PackageExportWizard_1, message);
			setResult(overwrite ? Status.OK_STATUS : Status.CANCEL_STATUS);
		}

		public IStatus getResult() {
			return result;
		}

		public void setResult(IStatus result) {
			this.result = result;
		}
	}

	private PackageExportPage parametersPage;

	public PackageExportWizard() {
		setDialogSettings(getSettingsSection());
		parametersPage = new PackageExportPage();
	}

	public IDialogSettings getSettingsSection() {
		IDialogSettings master = Activator.getDefault().getDialogSettings();
		String name = getClass().getSimpleName();
		IDialogSettings settings = master.getSection(name);
		if (settings == null) {
			settings = master.addNewSection(name);
		}
		return settings;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(false);
		setWindowTitle(Messages.exportWizard_Titile);
		setDefaultPageImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_EXPORT_WIZARD));

		if (selection != null) {
			parametersPage.setInitialSelection(getValidSelection(selection));
		}
	}

	public void setInitialSelection(List<IProject> resources) {
		if (resources != null) {
			parametersPage.setInitialSelection(resources);
		}
	}

	@Override
	public void addPages() {
		addPage(parametersPage);
	}

	@Override
	public boolean performFinish() {
		saveSettings();
		
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return false;
		}
		
		final IProject project = parametersPage.getSelectedProject();
		final File directory = new File(parametersPage.getDestinationDirectory());
		final boolean overwrite = parametersPage.isOverwriteSelected();
		final boolean forProduction = parametersPage.isProductionModeSelected();
		final String appConfigsPath = parametersPage.getConfigsDirectory();
		
		Job createPackageJob = new Job(Messages.exportWizard_JobTitle) {
			private StatusChangeListener listener;

			public IStatus run(IProgressMonitor monitor) {
				listener = new StatusChangeListener(monitor);
				if (monitor.isCanceled()) {
					return Status.OK_STATUS;
				}
				
				File container = new File(project.getLocation()
						.toOSString());
				IMappingLoader mappingLoader = new EclipseMappingModelLoader();
				
				PackageBuilder builder = (forProduction)
						? new ProductionPackageBuilder(container, mappingLoader, appConfigsPath)
						: new PackageBuilder(container, mappingLoader);
				builder.addStatusChangeListener(listener);
				builder.setVariableResolver(new EclipseVariableResolver());
				if (!overwrite) {
					IStatus s = shouldOverwrite(directory, builder);
					if (!s.isOK()) {
						return s;
					}
				}

				builder.createDeploymentPackage(directory);
					
				if (monitor.isCanceled()) {
					return Status.OK_STATUS;
				}
				return new SdkStatus(listener.getStatus());
			}

			protected IStatus shouldOverwrite(final File directory,
					PackageBuilder builder) {
				try {
					final File p = builder.getDeploymentPackageFile(directory);
					if (p.exists()) {
						final OverwriteRunnable runnable = new OverwriteRunnable(
								p);
						Display.getDefault().syncExec(runnable);
						return runnable.getResult();
					}
				} catch (IOException e) {
					return new Status(Status.ERROR, Activator.PLUGIN_ID,
							e.toString());
				}
				return Status.OK_STATUS;
			}
		};
		createPackageJob.setUser(true);
		createPackageJob.schedule();
		return true;
	}

	private void saveSettings() {
		parametersPage.saveSettings();
	}

	protected List<IProject> getValidSelection(
			IStructuredSelection currentSelection) {

		IStructuredSelection structuredSelection = (IStructuredSelection) currentSelection;
		List<IProject> selectedElements = new ArrayList<IProject>(
				structuredSelection.size());
		Iterator<?> iter = structuredSelection.iterator();
		while (iter.hasNext()) {
			Object selectedElement = iter.next();
			IProject project = null;
			Object projectAdapter = ((IAdaptable) selectedElement).getAdapter(IProject.class);
			if (projectAdapter instanceof IProject) {
				project = (IProject) projectAdapter;
			} else if (selectedElement instanceof IContainer) {
				project = ((IContainer) selectedElement).getProject();
			} else if (selectedElement instanceof IFile) {
				project = ((IFile) selectedElement).getProject();
			}
			if (project != null) {
				if (project
						.findMember(DescriptorContainerManager.DESCRIPTOR_PATH) != null) {
					selectedElements.add(project);
				}
			}
		}
		return selectedElements;
	}

}
