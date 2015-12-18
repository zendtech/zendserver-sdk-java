package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.targets.messages"; //$NON-NLS-1$
	public static String ZendServerCredentialsDialog_0;
	public static String ZendServerCredentialsDialog_1;
	public static String ZendServerCredentialsDialog_Application;
	public static String ZendServerManager_ConfigurationFilesNotFound_Error;
	public static String ZendServerManager_ErrorReadingInstallationParameters_Error;
	public static String ZendServerManager_InstallationLocationNotFound_Error;
	public static String ZendServerManager_InstallationLocationNotValid_Error;
	public static String ZendServerManager_SetupPathMapping_NoBundleAvailable_Error;
	public static String ZendServerManager_SetupPathMapping_NoServerLocationAvialable_Error;
	public static String ZendServerManager_UnsupportedOS_Error;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
