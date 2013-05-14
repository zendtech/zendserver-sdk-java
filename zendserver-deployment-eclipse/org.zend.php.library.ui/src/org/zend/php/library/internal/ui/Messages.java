package org.zend.php.library.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.library.internal.ui.messages"; //$NON-NLS-1$
	public static String DeployLibraryContribution_Title;
	public static String DeployLibraryHandler_WarningMessage;
	public static String DeployLibraryHandler_WarningTitle;
	public static String LibraryConfigurationBlock_AddPHPLibrary;
	public static String LibraryConfigurationBlock_DeployTo;
	public static String LibraryConfigurationBlock_Name;
	public static String LibraryConfigurationBlock_NoTargetMessage;
	public static String LibraryConfigurationBlock_Version;
	public static String LibraryConfigurationBlock_WarnRedeploy;
	public static String LibraryConfigurationPage_Title;
	public static String LibraryDeploymentUtils_ConflictMessage;
	public static String LibraryDeploymentUtils_No;
	public static String LibraryDeploymentUtils_WarningTitle;
	public static String LibraryDeploymentUtils_Yes;
	public static String LibraryDeploymentWizard_Description;
	public static String LibraryDeploymentWizard_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
