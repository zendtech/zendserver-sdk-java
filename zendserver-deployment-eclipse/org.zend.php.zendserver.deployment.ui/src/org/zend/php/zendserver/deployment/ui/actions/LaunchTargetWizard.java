package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.ui.IWorkbench;
import org.zend.php.zendserver.deployment.ui.Activator;

public class LaunchTargetWizard implements Runnable {

	
	public void launchWizard(IWorkbench workbench) {
		AddTargetAction action = new AddTargetAction();
		action.run();
	}
	
	public void run() {
		IWorkbench w = Activator.getDefault().getWorkbench();
		launchWizard(w);
	}

}
