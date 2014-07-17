/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.zend.php.zendserver.deployment.debug.ui.messages"; //$NON-NLS-1$

	public static String deploymentWizard_LaunchTitle;
	public static String deploymentWizard_DebugTitle;
	public static String deploymentWizard_DeployTitle;

	public static String runContribution_LaunchingPHPApp;
	public static String debugContribution_LaunchingPHPApp;
	public static String deployContribution_DeployPHPApp;

	public static String deploymentTab_Title;

	public static String configurationPage_Name;
	public static String configurationPage_Title;
	public static String configurationPage_AddTarget;
	public static String configurationPage_DeployTo;
	public static String configurationPage_DeployToTooltip;
	public static String configurationPage_baseURL;
	public static String configurationPage_ignoreFailures;
	public static String configurationPage_ignoreFailuresTooltip;
	public static String configurationPage_ValidationError_TargetLocation;
	public static String configurationPage_ValidationError_BaseUrl;

	public static String parametersPage_Description;
	public static String parametersPage_Name;
	public static String parametersPage_Title;
	public static String parametersPage_applicationParams;

	public static String updateExistingApplicationDialog_Title;
	public static String updateExistingApplicationDialog_Message;
	public static String updateExistingApplicationDialog_yesButton;
	public static String updateExistingApplicationDialog_noButton;

	public static String advancedSection_Label;
	public static String advancedSection_Deploy;
	public static String advancedSection_Update;
	public static String advancedSection_AutoDeploy;
	public static String advancedSection_NoAction;
	public static String advancedSection_autoDeployComboLabel;
	public static String advancedSection_autoDeployComboTooltip;
	public static String advancedSection_Title;
	public static String advancedSection_updateComboLabel;
	public static String advancedSection_updateComboTooltip;

	public static String applicationConflictDialog_Message;

	public static String applicationConflictDialog_Title;

	public static String ConfigurationBlock_ApplicationNameLabel;

	public static String ConfigurationBlock_DevelopmentModeLabel;
	public static String ConfigurationBlock_DevelopmentModeDesc;

	public static String ConfigurationBlock_LocalAppConflictErrorMessage;

	public static String ConfigurationBlock_RefreshLabel;

	public static String ConfigurationBlock_UrlEmptyError;

	public static String ConfigurationBlock_UrlWhitespacesError;

	public static String ConfigurationBlock_WarnUpdatingLabel;

	public static String DebugModeAction_NoTargetMessage;

	public static String DebugModeAction_StartLabel;

	public static String DebugModeAction_StopLabel;

	public static String DebugModeCompositeFragment_AddLabel;

	public static String DebugModeCompositeFragment_BreakAtFirstLabel;

	public static String DebugModeCompositeFragment_CreateTitle;

	public static String DebugModeCompositeFragment_DebugModeDesc;

	public static String DebugModeCompositeFragment_Name;

	public static String DebugModeCompositeFragment_EditLabel;

	public static String DebugModeCompositeFragment_EditTitle;

	public static String DebugModeCompositeFragment_EnterFilter;

	public static String DebugModeCompositeFragment_FilterSectionLabel;

	public static String DebugModeCompositeFragment_InvalidPort;

	public static String DebugModeCompositeFragment_InvalidUrl;

	public static String DebugModeCompositeFragment_ModifyFilter;

	public static String DebugModeCompositeFragment_PageDesc;

	public static String DebugModeCompositeFragment_RemoveLabel;

	public static String DebugModeCompositeFragment_RestartJobDesc;

	public static String DebugModeCompositeFragment_RestartJobTitle;

	public static String DebugModeCompositeFragment_RestartQuestion;

	public static String DebugModeHandler_DebugModeLabel;

	public static String DeploymentHandler_ApplicationNameErrorMessage;

	public static String DeploymentHandler_sshTunnelErrorMessage;
	public static String DeploymentHandler_sshTunnelErrorTitle;

	public static String updateExistingAppDevCloudDialog_Message;

	public static String DeploymentLaunchConfigurationTab_EnableDeployment;

	public static String DeploymentParameters_Title;

	public static String DeploymentWizard_DebugDesc;

	public static String DeploymentWizard_DeployDesc;

	public static String DeploymentWizard_LaunchDesc;

	public static String EmailValidator_InvalidEmail;

	public static String LaunchApplicationHandler_0;

	public static String LaunchApplicationHandler_1;
	public static String NumberValidator_NotANumber;
	public static String OpenTunnelCommand_Message;

	public static String OpenTunnelCommand_SuccessMessage;

	public static String OpenTunnelCommand_NotSupportedMessage;

	public static String OpenTunnelCommand_OpenTunnelTitle;

	public static String OpenTunnelCommand_Title;

	public static String OpenTunnelCommand_TunnelOpenedMessage;

	public static String OpenTunnelCommand_UnknownContainer;

	public static String PasswordValidator_InvalidPassword;

	public static String ParametersBlock_ExportButton;
	public static String ParametersBlock_ImportButton;
	public static String ParametersBlock_ImportDialogDescription;
	public static String ParametersBlock_ExportDialogDescription;
	public static String ParametersBlock_ImportDialogTitle;
	
	public static String DebugModePreferencesPage_AddButton;
	public static String DebugModePreferencesPage_AddDesc;
	public static String DebugModePreferencesPage_EdiotButton;
	public static String DebugModePreferencesPage_FilterSectionLabel;
	public static String DebugModePreferencesPage_JobDescription;

	public static String DebugModePreferencesPage_JobTitle;

	public static String DebugModePreferencesPage_ModifyDesc;
	public static String DebugModePreferencesPage_RemoveButton;

	public static String DebugModePreferencesPage_RestartMessage;
	public static String DebugModePreferencesPage_TargetLabel;

	public static String DebugModePreferencesPage_URLValidationError;

	public static String DebugModePreferencesPage_URLValidationPortError;

	public static String SSHTunnelAction_OpenLabel;

	public static String StartDebugModeHandler_DebugStartedQuestionMessage;

	public static String DebugModeHandler_StartingDebugMode;

	public static String DebugModeHandler_StoppingDebugMode;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
