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
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
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
	private static final String TMP_SCRIPT_NAME = "studio_debugger_detect.php"; //$NON-NLS-1$
	private static final String ZEND_DEBUGGER_ID = "zend_debugger"; //$NON-NLS-1$
	private static final String XDEBUG_ID = "xdebug"; //$NON-NLS-1$

	/**
	 * Detect debugger for a local server instance.
	 * 
	 * @param server
	 *            {@link Server} instance for a local server
	 * @return debugger id for specified local server; if cannot detect then
	 *         return <code>Zend Debugger</code> id.
	 * @throws IOException
	 */
	public static String getLocalDebuggerId(Server server) throws IOException {
		Properties props = executeValidationScript(server);
		if (props.containsKey(ZEND_DEBUGGER_ID)) {
			return DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID;
		}
		if (props.containsKey(XDEBUG_ID)) {
			return XDebugCommunicationDaemon.XDEBUG_DEBUGGER_ID;
		}
		return PHPDebuggersRegistry.NONE_DEBUGGER_ID;
	}

	private static synchronized Properties executeValidationScript(Server server) throws IOException {
		Properties properties = new Properties();

		InetAddress address = InetAddress.getByName(server.getHost());
		if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()) {
			return properties;
		}

		String docRoot = server.getDocumentRoot();
		if (docRoot.isEmpty() || !new File(docRoot).exists()) {
			return properties;
		}

		Bundle bundle = Platform.getBundle(ServersUI.PLUGIN_ID);
		BufferedReader input = null;
		BufferedWriter output = null;
		File tempScriptFile = new File(docRoot + File.separator
				+ TMP_SCRIPT_NAME);
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
					+ TMP_SCRIPT_NAME).openConnection();
			input = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			properties.load(input);
			return properties;
		} finally {
			if (tempScriptFile.exists()) {
				tempScriptFile.delete();
				tempScriptFile.deleteOnExit();
			}
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				// should not occur; but if it does
				// login it
				ServersUI.logError(e);
			}
		}
	}

}
