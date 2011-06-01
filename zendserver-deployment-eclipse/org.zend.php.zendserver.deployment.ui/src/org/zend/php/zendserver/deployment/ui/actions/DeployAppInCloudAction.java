package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.Activator;


public class DeployAppInCloudAction extends Action {

	public DeployAppInCloudAction() {
		super();
		setText("Zend Cloud");
		setToolTipText("Deploy application to Zend Cloud");
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_ZENDCLOUD_APPLICATION));
	}
	
	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openInformation(shell, "Deploy in Cloud Application", "Not ready yet!");
		// TODO
	}
}
