package org.zend.php.common.welcome;

import org.eclipse.ui.IWorkbench;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;

public class LaunchTargetWizard extends AbstractWelcomePageListener {

	@Override
	public void launchWizard(IWorkbench workbench) {
		AddTargetAction action = new AddTargetAction();
		action.run();
	}

}
