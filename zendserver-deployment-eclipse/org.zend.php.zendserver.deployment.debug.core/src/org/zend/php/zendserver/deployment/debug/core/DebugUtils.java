/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core;

import java.net.MalformedURLException;

import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.xdebug.dbgp.XDebugDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.server.core.Logger;
import org.zend.sdklib.internal.application.ZendConnection;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ExtensionInfo;
import org.zend.webapi.core.connection.data.ExtensionsList;

@SuppressWarnings("restriction")
public class DebugUtils {

	private static final String ZEND_DEBUGGER_EXT_NAME = "zend debugger"; //$NON-NLS-1$
	private static final String XDEBUG_EXT_NAME = "xdebug"; //$NON-NLS-1$

	/**
	 * Detects and returns debugger type id that is installed on top of given Zend
	 * target. It uses Web API calls to fetch the appropriate data.
	 * 
	 * @param target
	 * @return debugger type id
	 */
	public static final String getDebuggerId(IZendTarget target) {
		if (target == null)
			return PHPDebuggersRegistry.NONE_DEBUGGER_ID;
		ZendConnection zendConnection = new ZendConnection() {
		};
		ExtensionsList extensionsList;
		try {
			WebApiClient webApiClient = zendConnection.getClient(target);
			extensionsList = webApiClient.extensionList(ZEND_DEBUGGER_EXT_NAME);
			if (extensionsList.getExtensionsInfo() != null) {
				ExtensionInfo extensionInfo = extensionsList.getExtensionsInfo().get(0);
				if (extensionInfo.isInstalled() && extensionInfo.isLoaded())
					return ZendDebuggerConfiguration.ID;
			}
			extensionsList = webApiClient.extensionList(XDEBUG_EXT_NAME);
			if (extensionsList.getExtensionsInfo() != null) {
				ExtensionInfo extensionInfo = extensionsList.getExtensionsInfo().get(0);
				if (extensionInfo.isInstalled() && extensionInfo.isLoaded())
					return XDebugDebuggerConfiguration.ID;
			}
		} catch (MalformedURLException e) {
			Logger.logException(e);
		} catch (WebApiException e) {
			Logger.logException(e);
		}
		return PHPDebuggersRegistry.NONE_DEBUGGER_ID;
	}
	
}
