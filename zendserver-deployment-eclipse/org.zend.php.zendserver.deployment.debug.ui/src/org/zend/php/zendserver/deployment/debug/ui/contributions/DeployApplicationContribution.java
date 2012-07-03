package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class DeployApplicationContribution extends TestingSectionContribution {

	protected static final String DEPLOY_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.deployApplication"; //$NON-NLS-1$

	public DeployApplicationContribution() {
		super(DEPLOY_COMMAND, ILaunchManager.RUN_MODE, Messages.deployContribution_DeployPHPApp,
				Activator.IMAGE_DEPLOY_APPLICATION);
	}

}
