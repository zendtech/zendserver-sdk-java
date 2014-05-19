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
package org.zend.php.zendserver.deployment.debug.ui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.ui.actions.IActionContribution;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

/**
 * Contribution to the action which is responsible for opening SSH tunnel for
 * selected server.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class SSHTunnelAction extends AbstractTunnelHelper implements
		IActionContribution {

	private Server server;

	public void setServer(Server server) {
		this.server = server;
	}

	public String getLabel() {
		if (SSHTunnelManager.getManager().isConnected(server.getHost())) {
			return "Close SSH Tunnel";
		} else {
			return Messages.SSHTunnelAction_OpenLabel;
		}
	}

	public boolean isAvailable(Server server) {
		SSHTunnelConfiguration config = SSHTunnelConfiguration.read(server);
		return config.isEnabled();
	}

	public ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(Activator.IMAGE_SSH_TUNNEL);
	}

	public void run() {
		if (server != null) {
			if (SSHTunnelManager.getManager().isConnected(server.getHost())) {
				closeTunnel(server.getHost());
			} else {
				openTunnel(SSHTunnelConfiguration.read(server));
			}
		}
	}

}
