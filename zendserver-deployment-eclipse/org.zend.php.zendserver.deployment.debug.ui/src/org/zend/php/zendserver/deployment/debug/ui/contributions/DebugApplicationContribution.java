package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class DebugApplicationContribution extends TestingSectionContribution {
	
	protected static final String DEBUG_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.launchApplication"; //$NON-NLS-1$

	public DebugApplicationContribution() {
		super(DEBUG_COMMAND, ILaunchManager.DEBUG_MODE,
				Messages.debugContribution_LaunchingPHPApp, Activator
						.getImageDescriptor(Activator.IMAGE_DEBUG_APPLICATION));
	}
	
	public ProjectType getType() {
		return ProjectType.APPLICATION;
	}

}
