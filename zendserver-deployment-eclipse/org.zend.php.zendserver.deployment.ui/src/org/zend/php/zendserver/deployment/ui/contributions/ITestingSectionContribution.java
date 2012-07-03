package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.jface.resource.ImageDescriptor;

public interface ITestingSectionContribution {
	
	static String PROJECT_NAME = "projectName"; //$NON-NLS-1$
	static String MODE = "mode"; //$NON-NLS-1$
	static String TARGET_ID = "targetId"; //$NON-NLS-1$

	String getLabel();

	ImageDescriptor getIcon();

	String getCommand();
	
	String getMode();

}
