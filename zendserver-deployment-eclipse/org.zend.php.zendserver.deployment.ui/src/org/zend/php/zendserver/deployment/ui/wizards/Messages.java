package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.ui.wizards.messages"; //$NON-NLS-1$
	public static String deployWizardTitle;
	public static String deployWizardPage_Description;
	public static String descriptorPage_Title;
	public static String descriptorPage_Details;
	public static String descriptorPage_AppName;
	public static String descriptorPage_Folder;
	public static String descriptorPage_FolderBrowse;
	public static String descriptorPage_FolderBrowse_Title;
	public static String descriptorPage_FolderBrowse_Desc;
	public static String descriptorPage_Link;
	public static String parametersPage_Title;
	public static String parametersPage_TargetLocation;
	public static String parametersPage_DeployTo;
	public static String parametersPage_baseURL;
	public static String parametersPage_applicationParams;
	public static String parametersPage_ValidationError_TargetLocation;
	public static String parametersPage_ValidationError_BaseUrl;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
