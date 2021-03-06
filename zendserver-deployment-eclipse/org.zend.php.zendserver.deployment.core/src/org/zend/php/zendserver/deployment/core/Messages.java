package org.zend.php.zendserver.deployment.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.core.messages"; //$NON-NLS-1$
	public static String ConnectionState_Connected;
	public static String ConnectionState_Disconnected;
	public static String ConnectionState_Unavailable;
	public static String ConnectionState_WorkingOffline;
	public static String DeploymentDescriptor_CannotSetProperty;
	public static String EclipseSSH2Settings_CopyFileError;
	public static String EclipseSSH2Settings_CreateFileError;
	public static String FieldNotEmptyTester_MustNotBeEmpty;
	public static String FileExistsTester_FileNotExists;
	public static String JSCHPubKeyDecryptor_InvalidPassphrase;
	public static String JSCHPubKeyDecryptor_PassphrasePrompt;
	public static String JSCHPubKeyDecryptor_SshError;
	public static String JSCHPubKeyDecryptor_SshErrorTitle;
	public static String ModelContainer_UnknownList;
	public static String TargetsManagerService_RemoveTargetJob;
	public static String VersionTester_IsNotValidVersionNumber;

	public static String PassphraseDialog_Passphrase;
	public static String PreviewWizardPage_changeElementLabelProvider_textFormat;

	public static String Sdk_InvalidLocation;
	public static String SdkManager_InstallError;
	public static String SdkManager_UninstallError;
	public static String SSHTunnel_ConnectionError;
	public static String ZendServerDependency_UnknownGetDependency;
	public static String ZendServerDependency_UnknownSetDependency;
	
	public static String LibraryManager_UnknownLibraryFormat_Error;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
