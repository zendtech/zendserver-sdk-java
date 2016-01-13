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
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.ApiKeyDetector;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
@SuppressWarnings("restriction")
public class EclipseApiKeyDetector extends ApiKeyDetector {

	public EclipseApiKeyDetector(String username, String password,
			String serverUrl) {
		super(username, password, serverUrl);
	}

	public EclipseApiKeyDetector(String username, String password) {
		super(username, password, getLocalServerUrl());
	}

	public EclipseApiKeyDetector(String serverUrl) {
		super(serverUrl);
	}

	public EclipseApiKeyDetector() {
		super(getLocalServerUrl());
	}

	private static String getLocalServerUrl() {
		try {
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals("localhost")) { //$NON-NLS-1$
					String defaultPort = server.getAttribute(
							ZendServerManager.ZENDSERVER_GUI_URL_KEY, null);
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
		return "http://localhost:10081/ZendServer"; //$NON-NLS-1$
	}

	public void getServerCredentials(final String serverName,
			final String message) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				ZendServerCredentialsDialog dialog = new ZendServerCredentialsDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Zend Server Credentials", message); //$NON-NLS-1$
				if (dialog.open() == Window.OK) {
					setUsername(dialog.getUsername());
					setPassword(dialog.getPassword());
				}
			}
		});
	}

}
