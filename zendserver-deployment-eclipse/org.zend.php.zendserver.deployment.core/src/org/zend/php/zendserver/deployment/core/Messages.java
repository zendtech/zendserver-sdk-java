package org.zend.php.zendserver.deployment.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.messages"; //$NON-NLS-1$
	public static String FieldNotEmptyTester_MustNotBeEmpty;
	public static String FileExistsTester_FileNotExists;
	public static String VersionTester_IsNotValidVersionNumber;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
