package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;

public class LaunchConfigurationDoubleClickListener implements IDoubleClickListener {

	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Object obj = sselection.getFirstElement();
		final ILaunchConfiguration config = (ILaunchConfiguration) obj;
		Job job = new Job("Deployment Wizard") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				DeploymentHandler handler = new DeploymentHandler(config);
				if (handler.openDeploymentWizard() == IStatus.OK) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.setSystem(true);
		job.schedule();
	}
	
}
