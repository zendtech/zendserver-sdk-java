package org.zend.php.zendserver.deployment.ui.servers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.ui.servers.messages"; //$NON-NLS-1$
	public static String AbstarctCloudCompositeFragment_GenerateKeyMessage;
	public static String AbstarctCloudCompositeFragment_GenerateKeyTitle;
	public static String WebApiCompositeFragment_Title;
	public static String WebApiCompositeFragment_Description;
	public static String WebApiCompositeFragment_DetectingCredentials;
	public static String WebApiCompositeFragment_DetectLabel;
	public static String WebApiCompositeFragment_EmptyHostMessage;
	public static String WebApiCompositeFragment_EmptyKeyMessage;
	public static String WebApiCompositeFragment_EmptySecretMessage;
	public static String WebApiCompositeFragment_EnableLabel;
	public static String WebApiCompositeFragment_Host;
	public static String WebApiCompositeFragment_InvalidCredentialsError;
	public static String WebApiCompositeFragment_KeyName;
	public static String WebApiCompositeFragment_KeySecret;
	public static String WebApiCompositeFragment_TestingConnection;
	public static String WebApiCompositeFragment_Name;
	public static String WebApiCompositeFragment_WebApiDetails;
	public static String WebApiTester_NullTarget;
	public static String WebApiTester_TestingPortSubTask;
	public static String WebApiTester_UnexpectedError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
