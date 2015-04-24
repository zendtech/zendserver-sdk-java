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
package org.zend.php.server.internal.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.debug.core.debugger.launching.ILaunchDelegateListener;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;

/**
 * {@link ILaunchDelegateListener} for debugging PHP applications. It is
 * responsible for establishing SSH tunnel connection.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServerLaunchDelegateListener implements ILaunchDelegateListener {

	@Override
	public int preLaunch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) {
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			String serverName = null;
			try {
				serverName = configuration.getAttribute(Server.NAME,
						(String) null);
				if (serverName != null) {
					Server server = ServersManager.getServer(serverName);
					if (server != null) {
						SSHTunnelConfiguration sshConfig = SSHTunnelConfiguration
								.read(server);
						if (sshConfig.isEnabled()) {
							monitor.subTask(Messages.ServerLaunchDelegateListener_SubTaskName);
							SSHTunnelManager.getManager().connect(sshConfig);
						}
					}
				}
			} catch (CoreException e) {
				ServersUI.logError(e);
			} catch (Exception e) {
				ServersUI.logError(e);
				displayError(serverName);
				return -1;
			}
		}
		return 0;
	}

	private void displayError(final String name) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog
						.openError(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(),
								Messages.ServerLaunchDelegateListener_ErrorTitle,
								MessageFormat
										.format(Messages.ServerLaunchDelegateListener_ErrorMessage,
												name));
			}
		});
	}

}
