package org.zend.php.zendserver.deployment.ui.servers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.ui.servers.messages"; //$NON-NLS-1$
	public static String AbstarctCloudCompositeFragment_GenerateKeyMessage;
	public static String AbstarctCloudCompositeFragment_GenerateKeyTitle;
	public static String DeploymentCompositeFragment_CreateTitle;
	public static String DeploymentCompositeFragment_Description;
	public static String DeploymentCompositeFragment_DetectingCredentials;
	public static String DeploymentCompositeFragment_DetectLabel;
	public static String DeploymentCompositeFragment_EditTitle;
	public static String DeploymentCompositeFragment_EmptyHostMessage;
	public static String DeploymentCompositeFragment_EmptyKeyMessage;
	public static String DeploymentCompositeFragment_EmptySecretMessage;
	public static String DeploymentCompositeFragment_EnableLabel;
	public static String DeploymentCompositeFragment_Host;
	public static String DeploymentCompositeFragment_InvalidCredentialsError;
	public static String DeploymentCompositeFragment_KeyName;
	public static String DeploymentCompositeFragment_KeySecret;
	public static String DeploymentCompositeFragment_TestingConnection;
	public static String DeploymentCompositeFragment_Name;
	public static String DeploymentCompositeFragment_WebApiDetails;
	public static String DeploymentTester_NullTarget;
	public static String DeploymentTester_TestingPortSubTask;
	public static String DeploymentTester_UnexpectedError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
