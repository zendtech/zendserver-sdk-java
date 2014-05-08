package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.targets.messages"; //$NON-NLS-1$
	public static String ZendServerCredentialsDialog_0;
	public static String ZendServerCredentialsDialog_1;
	public static String ZendServerCredentialsDialog_Application;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
