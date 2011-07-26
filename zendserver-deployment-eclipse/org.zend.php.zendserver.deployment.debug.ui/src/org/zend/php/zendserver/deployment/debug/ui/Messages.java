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

	public static String deploymentDialog_Title;
	public static String deploymentDialog_Message;
	public static String deploymentJob_Title;
	public static String deploymentStatusJob_Title;
	public static String updateJob_Title;

	public static String runContribution_LaunchingPHPApp;
	public static String debugContribution_LaunchingPHPApp;

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

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
