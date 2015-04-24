/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.ui.types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Random;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.php.internal.debug.core.xdebug.communication.XDebugCommunicationDaemon;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.eclipse.php.internal.server.core.Server;
import org.osgi.framework.Bundle;
import org.zend.php.server.ui.ServersUI;

/**
 * Utility class which supports server type processing.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServerTypeUtils {

	private static final String DEBUGGER_SCRIPT = "resources/debugger_validation.php"; //$NON-NLS-1$
	private static final String XDEBUG_ID = "xdebug"; //$NON-NLS-1$

	/**
	 * Detect debugger for a local server instance.
	 * 
	 * @param server
	 *            {@link Server} instance for a local server
	 * @return debugger id for specified local server; if cannot detect then
	 *         return <code>Zend Debugger</code> id.
	 */
	public static String getLocalDebuggerId(Server server) {
		String id = DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID;
		Properties props = executeValidationScript(server);
		if (props != null) {
			if (props.containsKey(XDEBUG_ID)) {
				id = XDebugCommunicationDaemon.XDEBUG_DEBUGGER_ID;
			}
		}
		return id;
	}

	private static Properties executeValidationScript(Server server) {
		try {
			InetAddress address = InetAddress.getByName(server.getHost());
			if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()) {
				return null;
			}
		} catch (UnknownHostException e) {
			// ignore and skip debugger validation
			return null;
		}
		String docRoot = server.getDocumentRoot();
		if (docRoot.isEmpty() || !new File(docRoot).exists()) {
			return null;
		}
		Bundle bundle = Platform.getBundle(ServersUI.PLUGIN_ID);
		BufferedReader input = null;
		BufferedWriter output = null;
		String tempScriptName = +new Random().nextLong() + ".php"; //$NON-NLS-1$
		File tempScriptFile = new File(docRoot + File.separator
				+ tempScriptName);
		try {
			input = new BufferedReader(new InputStreamReader(
					FileLocator.openStream(bundle, new Path(DEBUGGER_SCRIPT),
							false)));
			output = new BufferedWriter(new FileWriter(tempScriptFile));
			String line = null;
			while ((line = input.readLine()) != null) {
				output.append(line);
				output.append('\n');
			}
			input.close();
			output.close();
			URLConnection connection = new URL(server.getRootURL() + "/" //$NON-NLS-1$
					+ tempScriptName).openConnection();
			input = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			Properties properties = new Properties();
			properties.load(input);
			return properties;
		} catch (IOException e) {
			ServersUI.logError(e);
		} finally {
			if (tempScriptFile.exists()) {
				tempScriptFile.delete();
			}
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				ServersUI.logError(e);
			}
		}
		return null;
	}

}
