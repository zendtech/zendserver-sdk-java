package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.swt.graphics.Image;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.ui.contributions.ITestingSectionContribution;

public class TestingSectionContribution implements ITestingSectionContribution {
	
	public static final String PROJECT_NAME = "projectName"; //$NON-NLS-1$
	public static final String MODE = "mode"; //$NON-NLS-1$
	public static final String TARGET_ID = "targetId"; //$NON-NLS-1$
	
	private String label;
	private String command;
	private String mode;
	private String image;

	public TestingSectionContribution(String command, String mode,
			String label, String image) {
		super();
		this.label = label;
		this.command = command;
		this.mode = mode;
		this.image = image;
	}

	public String getLabel() {
		return label;
	}

	public Image getIcon() {
		return Activator.getImageDescriptor(image).createImage();
	}

	public String getCommand() {
		return command;
	}

	public String getMode() {
		return mode;
	}

}
