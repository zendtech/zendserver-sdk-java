package org.zend.php.zendserver.deployment.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.messages"; //$NON-NLS-1$
	public static String ConnectionState_Connected;
	public static String ConnectionState_Disconnected;
	public static String ConnectionState_Unavailable;
	public static String ConnectionState_WorkingOffline;
	public static String FieldNotEmptyTester_MustNotBeEmpty;
	public static String FileExistsTester_FileNotExists;
	public static String TargetDatabase_1;
	public static String TargetDatabase_ProfileName;
	public static String VersionTester_IsNotValidVersionNumber;

	public static String ZendDevCloudTunnel_1;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
