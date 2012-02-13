package org.zend.php.common.welcome;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.zend.php.ui.welcomePage.Messages"; //$NON-NLS-1$

	public static String WelcomePageTitle;
	public static String FeatureManagementSectionDescription;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
