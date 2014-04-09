/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.ApiKeyDetector;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EclipseApiKeyDetector extends ApiKeyDetector {

	private static final String ZENDSERVER_GUI_URL_KEY = "zendserver_default_port"; //$NON-NLS-1$

	private String username;
	private String password;

	public EclipseApiKeyDetector(String username, String password) {
		super(username, password, getLocalServerUrl());
	}
	
	public EclipseApiKeyDetector() {
		super(getLocalServerUrl());
	}

	@SuppressWarnings("restriction")
	private static String getLocalServerUrl() {
		try {
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals("localhost")) { //$NON-NLS-1$
					String defaultPort = server.getAttribute(
							ZENDSERVER_GUI_URL_KEY, null);
					if (defaultPort != null) {
						return "http://localhost:" + defaultPort //$NON-NLS-1$
								+ "/ZendServer"; //$NON-NLS-1$
					}
				}
			}
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// do nothing and return null
		}
		return null;
	}

	public String[] getServerCredentials(final String message) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				ZendServerCredentialsDialog dialog = new ZendServerCredentialsDialog(
						Display.getDefault().getActiveShell(),
						"Zend Server Credentials", message); //$NON-NLS-1$
				if (dialog.open() == Window.OK) {
					username = dialog.getUsername();
					password = dialog.getPassword();
				} else {
					username = null;
					password = null;
				}
			}
		});
		if (username == null || username.trim().isEmpty() || password == null
				|| password.trim().isEmpty()) {
			return null;
		}
		return new String[] { username, password };
	}

}
