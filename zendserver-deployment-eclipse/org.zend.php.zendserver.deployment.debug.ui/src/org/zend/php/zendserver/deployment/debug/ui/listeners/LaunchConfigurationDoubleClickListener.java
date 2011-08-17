package org.zend.php.zendserver.deployment.debug.ui.listeners;

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
		ILaunchConfiguration config = (ILaunchConfiguration) obj;
		DeploymentHandler handler = new DeploymentHandler(config);
		handler.openDeploymentWizard();
	}
	
}
