package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class DebugApplicationContribution extends ApplicationContribution {
	
	protected static final String DEBUG_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.launchApplication"; //$NON-NLS-1$

	public DebugApplicationContribution() {
		super(DEBUG_COMMAND, ILaunchManager.DEBUG_MODE, Messages.debugContribution_LaunchingPHPApp,
				Activator.IMAGE_DEBUG_APPLICATION);
	}

}
