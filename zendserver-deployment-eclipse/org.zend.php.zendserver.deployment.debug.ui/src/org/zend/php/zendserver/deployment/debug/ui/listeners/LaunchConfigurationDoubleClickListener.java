package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;

public class LaunchConfigurationDoubleClickListener implements IDoubleClickListener {

	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Object obj = sselection.getFirstElement();
		ILaunchConfiguration config = (ILaunchConfiguration) obj;
		
		final DeploymentWizard wizard = new DeploymentWizard(config);
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.setPageSize(550, 350);
		dialog.create();
		if (dialog.open() == Window.OK) {
			Job job;
			IDeploymentHelper updatedHelper = wizard.getHelper();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(updatedHelper.getProjectName());
			switch (updatedHelper.getOperationType()) {
			case IDeploymentHelper.DEPLOY:
				job = new DeployLaunchJob(updatedHelper, project);
				break;
			case IDeploymentHelper.UPDATE:
				job = new UpdateLaunchJob(updatedHelper, project);
				break;
			default:
				return;
			}
			job.setUser(true);
			job.schedule();
		}
	}
	
}
