package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class RunApplicationContribution extends TestingSectionContribution {

	protected static final String RUN_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.launchApplication"; //$NON-NLS-1$

	public RunApplicationContribution() {
		super(RUN_COMMAND, ILaunchManager.RUN_MODE,
				Messages.runContribution_LaunchingPHPApp, Activator
						.getImageDescriptor(Activator.IMAGE_RUN_APPLICATION));
	}
	
	public ProjectType getType() {
		return ProjectType.APPLICATION;
	}

}
