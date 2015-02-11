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

import java.net.MalformedURLException;

import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.core.utils.LocalApacheDetector;

/**
 * Utility class for a local Apache HTTP Server.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ApacheUtil {

	public static final String LOCATION = "apache2Location"; //$NON-NLS-1$

	private static final String DEFAULT_BASE_URL = "http://localhost"; //$NON-NLS-1$

	/**
	 * Parse document root and base URL settings from httpd.conf file for
	 * specified server.
	 * 
	 * @param server
	 *            {@link Server} instance
	 * @return <code>true</code> if configuration was detected successfully;
	 *         otherwise return <code>false</code>
	 */
	public static boolean parseAttributes(Server server) {
		String location = server.getAttribute(LOCATION, null);
		LocalApacheDetector detector = new LocalApacheDetector(location);
		if (detector.detect()) {
			try {
				server.setBaseURL(DEFAULT_BASE_URL);
			} catch (MalformedURLException e) {
				// Cannot occur
			}
			server.setPort(detector.getPort());
			server.setDocumentRoot(detector.getDocumentRoot());
			server.setDebuggerId(ServerTypeUtils.getLocalDebuggerId(server));
			return true;
		}
		return false;
	}

}
