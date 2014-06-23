package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;

public interface IProductionSectionContribution {
	
	static String PROJECT_NAME = "projectName"; //$NON-NLS-1$
	static String TARGET_ID = "targetId"; //$NON-NLS-1$

	String getLabel();

	ImageDescriptor getIcon();

	String getCommand();
	
	ProjectType getType();

}
