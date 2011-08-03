package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.debug.ui.Activator;

public class DeployApplicationContribution extends ApplicationContribution {

	protected static final String DEPLOY_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.deployApplication"; //$NON-NLS-1$

	public DeployApplicationContribution() {
		super(DEPLOY_COMMAND, ILaunchManager.RUN_MODE, "Deploy a PHP Applciation",
				Activator.IMAGE_DEPLOY_APPLICATION);
	}

}
