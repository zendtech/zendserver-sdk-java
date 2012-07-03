package org.zend.php.zendserver.deployment.ui.contributions;

import org.eclipse.swt.graphics.Image;

public interface ITestingSectionContribution {

	String getLabel();

	Image getIcon();

	String getCommand();
	
	String getMode();

}
