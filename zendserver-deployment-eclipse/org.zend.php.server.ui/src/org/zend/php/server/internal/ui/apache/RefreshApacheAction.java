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
package org.zend.php.server.internal.ui.apache;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.server.ui.types.ServerTypesManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.actions.IActionContribution;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * Contribution to action which is responsible for refreshing local Apache HTTP
 * Server configuration settings.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class RefreshApacheAction implements IActionContribution {

	private Server server;

	@Override
	public String getLabel() {
		return Messages.RefreshApacheAction_RefreshLabel;
	}

	@Override
	public ImageDescriptor getIcon() {
		return ServersUI.getImageDescriptor(ServersUI.REFRESH_APACHE_ICON);
	}

	@Override
	public boolean isAvailable(Server server) {
		return true;
	}

	@Override
	public void run() {
		if (server != null) {
			IServerType serverType = ServerTypesManager.getInstance().getType(
					server);
			if (LocalApacheType.ID.equals(serverType.getId())) {
				Job refreshJob = new Job(Messages.RefreshApacheAction_JobName) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask(Messages.RefreshApacheAction_JobDesc,
								IProgressMonitor.UNKNOWN);
						Server newServer = new Server();
						newServer.setName(server.getName());
						newServer.setAttribute(LocalApacheType.LOCATION, server
								.getAttribute(LocalApacheType.LOCATION, null));
						LocalApacheType.parseAttributes(newServer);
						final String name = checkBaseUrlConfilct(newServer);
						if (name != null) {
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									MessageDialog
											.openError(
													PlatformUI
															.getWorkbench()
															.getActiveWorkbenchWindow()
															.getShell(),
													Messages.RefreshApacheAction_BaseUrlTitle,
													MessageFormat
															.format(Messages.RefreshApacheAction_BaseUrlError,
																	name));
								}
							});
						} else {
							server.setPort(String.valueOf(newServer.getPort()));
							server.setDocumentRoot(newServer.getDocumentRoot());
							ServersManager.save();
						}
						monitor.done();
						return Status.OK_STATUS;
					}
				};
				refreshJob.setUser(true);
				refreshJob.schedule();
			}
		}
	}

	@Override
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Check if there is base URL conflict with existing server other then that
	 * which settings are being refreshed.
	 * 
	 * @param server
	 * @return name of conflicting server or <code>null</code> if there is no
	 *         base URL conflict
	 */
	private String checkBaseUrlConfilct(Server newServer) {
		Server[] servers = ServersManager.getServers();
		String baseUrl = newServer.getBaseURL();
		String name = newServer.getName();
		for (Server server : servers) {
			if (!name.equals(server.getName())
					&& baseUrl.equals(server.getBaseURL())) {
				return server.getName();
			}
		}
		return null;
	}

}
