package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.sdk.messages"; //$NON-NLS-1$
	public static String ProductionPackageBuilder_ErrorDeleteTempDirectory;
	public static String ProductionPackageBuilder_ErrorDeleteTempFile;
	public static String ProductionPackageBuilder_ErrorZFDeployTool;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
