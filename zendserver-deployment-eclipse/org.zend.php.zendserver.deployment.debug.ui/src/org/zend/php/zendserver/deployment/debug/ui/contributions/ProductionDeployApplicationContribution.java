package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contributions.IProductionSectionContribution;

public class ProductionDeployApplicationContribution implements
		IProductionSectionContribution {

	public String getLabel() {
		return Messages.deployContribution_DeployPHPApp;
	}

	public ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(Activator.IMAGE_DEPLOY_APPLICATION);
	}

	public String getCommand() {
		return "org.zend.php.zendserver.deployment.debug.ui.productionDeployApplication"; //$NON-NLS-1$
	}

	public ProjectType getType() {
		return ProjectType.APPLICATION;
	}

}
