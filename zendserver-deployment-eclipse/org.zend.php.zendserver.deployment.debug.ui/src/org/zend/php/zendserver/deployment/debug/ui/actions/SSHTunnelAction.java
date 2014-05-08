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
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

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
		return Messages.SSHTunnelAction_Label;
	}

	public boolean isAvailable(Server server) {
		IZendTarget target = getTarget();
		return getTarget() != null
				&& (TargetsManager.isOpenShift(target) || TargetsManager
						.isPhpcloud(target));
	}

	public ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(Activator.IMAGE_SSH_TUNNEL);
	}

	public void run() {
		IZendTarget target = getTarget();
		if (target != null) {
			if (TargetsManager.isOpenShift(target)
					|| TargetsManager.isPhpcloud(target)) {
				openTunnel(target);
			}
		}
	}

	private IZendTarget getTarget() {
		if (server != null) {
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			if (server != null) {
				String serverName = server.getName();
				IZendTarget[] targets = manager.getTargets();
				for (IZendTarget target : targets) {
					if (serverName.equals(target.getServerName())) {
						return target;
					}
				}
			}
		}
		return null;
	}

}
