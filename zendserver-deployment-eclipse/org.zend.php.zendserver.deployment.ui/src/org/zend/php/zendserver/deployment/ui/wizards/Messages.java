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
	public static String exportWizard_Titile;
	public static String exportWizard_JobTitle;
	public static String PackageExportPage_AlternativeConfigsRadioText;
	public static String PackageExportPage_Name;
	public static String PackageExportPage_Title;
	public static String PackageExportPage_Description;
	public static String PackageExportPage_ProductionCheckboxText;
	public static String PackageExportPage_ProjectCorruptedError;
	public static String PackageExportPage_ProjectListLabel;
	public static String PackageExportPage_BrowseButtonText;
	public static String PackageExportPage_ConfigDirectoryNotExistError;
	public static String PackageExportPage_ConfigLocationNotDirectoryError;
	public static String PackageExportPage_ConfigsDirectorySelectionDialogMessage;
	public static String PackageExportPage_ConfigsGroupText;
	public static String PackageExportPage_OverwriteCheckboxText;
	public static String PackageExportPage_DestinationDialogMessage;
	public static String PackageExportPage_DirectorySelectionDialogTitle;
	public static String PackageExportPage_DestinationGroupText;
	public static String PackageExportPage_DestinationNotDirectoryError;
	public static String PackageExportPage_DestinationNotExistError;
	public static String PackageExportPage_FileSystemButtonText;
	public static String PackageExportPage_NoDeploymentSupportWarning;
	public static String PackageExportPage_NoProjectAvailableError;
	public static String PackageExportPage_NoProjectSelectedError;
	public static String PackageExportPage_NotZF2ProjectError;
	public static String PackageExportPage_ReuseRadioText;
	public static String PackageExportPage_SelectConfigDirectoryMessage;
	public static String PackageExportPage_SelectDestinationMessage;
	public static String PackageExportPage_WorkspaceButtonText;
	public static String PackageExportPage_PHPLibraryError;
	public static String MySQLCredentialsDialog_DialogTitle;
	public static String NewDependencyWizard_0;
	public static String PackageExportWizard_0;
	public static String PackageExportWizard_1;

	public static String PortForwardingPage_CreateDesc;
	public static String PortForwardingPage_CreateTitle;
	public static String PortForwardingPage_EditDesc;
	public static String PortForwardingPage_EditTitle;
	public static String PortForwardingPage_InvalidLocalPortError;
	public static String PortForwardingPage_InvalidRemotePortError;
	public static String PortForwardingPage_LocalAddressLabel;
	public static String PortForwardingPage_LocalPortLabel;
	public static String PortForwardingPage_ProvideMessage;
	public static String PortForwardingPage_RemoteAddressLabel;
	public static String PortForwardingPage_RemotePortLabel;
	public static String PortForwardingPage_ResultLabel;
	public static String PortForwardingPage_SideLabel;
	public static String PortForwardingPage_SpecifyLocalAddressMessage;
	public static String PortForwardingPage_SpecifyLocalPortMessage;
	public static String PortForwardingPage_SpecifyRemoteAddressMessage;
	public static String PortForwardingPage_SpecifyRemotePortMessage;
	public static String PortForwardingWizard_CreateTitle;
	public static String PortForwardingWizard_EditTitle;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
