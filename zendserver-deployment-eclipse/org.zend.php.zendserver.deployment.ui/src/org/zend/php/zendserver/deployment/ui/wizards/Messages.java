/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
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
	public static String parametersPage_appUserName;
	public static String parametersPage_appUserNameTooltip;
	public static String parametersPage_defaultServer;
	public static String parametersPage_defaultServerTooltip;
	public static String parametersPage_ignoreFailures;
	public static String parametersPage_ignoreFailuresTooltip;
	public static String parametersPage_applicationParams;
	public static String parametersPage_ValidationError_TargetLocation;
	public static String parametersPage_ValidationError_BaseUrl;
	public static String exportWizard_Titile;
	public static String exportWizard_JobTitle;
	public static String exportPage_Description;
	public static String exportPage_Title;
	public static String exportPage_TableLabel;
	public static String exportPage_DestinationLabel;
	public static String exportPage_Browse;
	public static String exportPage_SelectAll;
	public static String exportPage_DeselectAll;
	public static String exportPage_DirectoryDialogMessage;
	public static String exportPage_DirectoryDialogTitle;
	public static String exportPage_TableError;
	public static String exportPage_DestinationError;
	public static String NewDependencyWizard_0;
	public static String PackageExportPage_0;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
