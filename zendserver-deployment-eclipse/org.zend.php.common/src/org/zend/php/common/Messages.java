/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.zend.php.common;

import org.eclipse.osgi.util.NLS;

/**
 * @author David Green
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.zend.php.common.messages"; //$NON-NLS-1$

	public static String ConnectorDiscoveryFailed;
	public static String ConnectorDiscoveryWizard_title;
	public static String ConnectorDiscoveryWizardMainPage_title;
	public static String ConnectorDiscoveryWizardMainPage_description;
	public static String ConnectorDiscoveryWizardMainPage_error_title;
	public static String ConnectorDiscoveryWizardMainPage_error_msg;
	public static String ConnectorDescriptorToolTip_detailsLink;
	public static String ConnectorDescriptorToolTip_detailsLink_tooltip;
	public static String Button_ApplyChanges;
	public static String Button_Restore;
	public static String UpdateCatalogJobName;
	public static String Message_with_cause;
	public static String Unexpected_exception;
	public static String ApplyChanges_JobName;
	public static String ApplyChanges_OperationComplete_Title;
	public static String ApplyChanges_OperationComplete_Msg;
	public static String CustomizationComponent_Description;
	public static String ModifyOperation_ComputeProfileChangeProgress;
	public static String ModifyOperation_InstallJobName;
	public static String ModifyOperation_ResolveJobName;
	public static String ProfileChangeOperation_NoProfileChangeRequest;
	public static String ProfileChangeOperation_ResolveTaskName;
	public static String ConnectorDiscovery_NoUrlDefined_Title;
	public static String ConnectorDiscovery_NoUrlDefined_Msg;
	public static String ConnectorDiscovery_InvalidUrlDefined_Title;
	public static String ConnectorDiscovery_InvalidUrlDefined_Msg;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// constructor
	}

}
