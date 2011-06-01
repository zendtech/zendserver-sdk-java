package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.Activator;


public class RunApplicationAction extends Action {

	public RunApplicationAction() {
		setText("Run");
		setToolTipText("Deploy application to Zend Server");
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_RUN_APPLICATION));
	}
	
	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openInformation(shell, "Run Application", "Not ready yet!");
		// TODO
	}
}
