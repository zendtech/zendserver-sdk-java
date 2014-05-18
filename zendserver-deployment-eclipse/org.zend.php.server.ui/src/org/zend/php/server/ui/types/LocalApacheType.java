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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.graphics.Image;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;

/**
 * Representation of local Apache HTTP Server type.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalApacheType implements IServerType {

	public static final String ID = "org.zend.php.server.ui.types.LocalApacheType"; //$NON-NLS-1$

	public static final String LOCATION = "apache2Location"; //$NON-NLS-1$

	private static final String DEFAULT_BASE_URL = "http://localhost"; //$NON-NLS-1$
	private static final String HTTPD_CONF_PATH = "/conf/httpd.conf"; //$NON-NLS-1$

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return Messages.LocalApacheType_Name;
	}

	@Override
	public Image getViewIcon() {
		return ServersUI.getDefault().getImage(ServersUI.APACHE_SERVER_ICON);
	}

	@Override
	public ImageDescriptor getWizardImage() {
		// TODO return correct apache wizard image
		return ServersUI.getImageDescriptor("icons/wizban/zend_server_wiz.png");
	}

	/**
	 * Parse document root and base URL settings from httpd.conf file for
	 * specified server.
	 * 
	 * @param server
	 *            {@link Server} instance
	 */
	public static void parseAttributes(Server server) {
		String location = server.getAttribute(LOCATION, null);
		if (location != null) {
			BufferedReader httpdReader = null;
			try {
				server.setBaseURL(DEFAULT_BASE_URL);
				httpdReader = new BufferedReader(new FileReader(new File(
						location, HTTPD_CONF_PATH)));
				String line = null;
				while ((line = httpdReader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("Listen")) { //$NON-NLS-1$
						String[] segments = line.trim().split(" "); //$NON-NLS-1$
						if (segments.length == 2) {
							server.setPort(segments[1]);
						}
					} else if (line.startsWith("DocumentRoot")) { //$NON-NLS-1$
						String[] segments = line.trim().split(" "); //$NON-NLS-1$
						if (segments.length == 2) {
							String val = segments[1];
							if (val.startsWith("\"")) { //$NON-NLS-1$
								server.setDocumentRoot(val.substring(1,
										val.length() - 1));
							}
						}
					}
				}
				server.setDebuggerId(ServerTypeUtils.getLocalDebuggerId(server));
			} catch (IOException e) {
			} finally {
				if (httpdReader != null) {
					try {
						httpdReader.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

}
