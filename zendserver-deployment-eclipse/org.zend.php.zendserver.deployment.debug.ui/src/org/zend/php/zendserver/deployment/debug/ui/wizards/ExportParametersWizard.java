package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class ExportParametersWizard extends BasicNewResourceWizard {

	public static final String WIZARD_ID = "org.zend php.zendserver.deployment.debug.ui.newParams"; //$NON-NLS-1$

	private WizardNewFileCreationPage mainPage;

	private IResource resource;
	private Map<String, String> params;

	public ExportParametersWizard(IResource resource, Map<String, String> params) {
		super();
		this.resource = resource;
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		mainPage = new WizardNewFileCreationPage("exportParams", new StructuredSelection(resource));//$NON-NLS-1$
		mainPage.setTitle(Messages.ExportParametersWizard_PageTitle);
		mainPage.setDescription(Messages.ExportParametersWizard_PageDescription);
		addPage(mainPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#init(org.eclipse
	 * .ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle(Messages.ExportParametersWizard_WizardTitile);
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#
	 * initializeDefaultPageImageDescriptor()
	 */
	protected void initializeDefaultPageImageDescriptor() {
		ImageDescriptor desc = Activator.getImageDescriptor(Activator.IMAGE_EXPORT_PARAMS_DEP);
		setDefaultPageImageDescriptor(desc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		final IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}
		Job job = new Job(Messages.ExportParametersWizard_JobTitle) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.ExportParametersWizard_JobDescription, IProgressMonitor.UNKNOWN);
				Properties props = new Properties();
				try {
					Set<Entry<String, String>> paramsSet = params.entrySet();
					for (Entry<String, String> param : paramsSet) {
						props.put(param.getKey(), param.getValue());
					}
					OutputStream out = new FileOutputStream(file.getLocation().toFile());
					props.store(out, "deployment parameters"); //$NON-NLS-1$
					out.close();
					file.refreshLocal(IResource.DEPTH_ZERO, monitor);
					monitor.done();
					return Status.OK_STATUS;
				} catch (IOException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ExportParametersWizard_ExportError_Message, e);
				} catch (CoreException e) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							Messages.ExportParametersWizard_ExportError_Message, e);
				}
			}
		};
		job.setUser(true);
		job.schedule();
		return true;
	}

}
