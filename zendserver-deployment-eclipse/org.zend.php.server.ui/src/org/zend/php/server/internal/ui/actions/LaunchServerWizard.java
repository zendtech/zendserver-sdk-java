package org.zend.php.server.internal.ui.actions;

import org.eclipse.ui.IWorkbench;
import org.zend.php.server.ui.ServersUI;

public class LaunchServerWizard implements Runnable {

	
	public void launchWizard(IWorkbench workbench) {
		AddServerAction action = new AddServerAction();
		action.run();
	}
	
	public void run() {
		IWorkbench w = ServersUI.getDefault().getWorkbench();
		launchWizard(w);
	}

}
