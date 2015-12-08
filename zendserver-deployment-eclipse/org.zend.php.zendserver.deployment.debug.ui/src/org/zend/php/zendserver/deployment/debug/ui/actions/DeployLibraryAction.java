/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.ui.actions.IActionContribution;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.wizards.LibraryDeploymentUtils;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

/**
 * Contribution to the action which is responsible for opening PHP library
 * deployment wizard.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DeployLibraryAction implements IActionContribution {

	private Server server;

	public DeployLibraryAction() {
	}

	@Override
	public String getLabel() {
		return Messages.DeployLibraryAction_Label;
	}

	@Override
	public ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(Activator.IMAGE_DEPLOY_LIBRARY);
	}

	@Override
	public void run() {
		LibraryDeploymentUtils util = new LibraryDeploymentUtils();
		util.openLibraryDeploymentWizard(getTarget(server));
	}

	@Override
	public void setServer(Server server) {
		this.server = server;
	}

	@Override
	public boolean isAvailable(Server server) {
		IZendTarget target = getTarget(server);
		return target != null
				&& TargetsManager.checkMinVersion(target,
						ZendServerVersion.byName("6.1.0")); //$NON-NLS-1$
	}

	private IZendTarget getTarget(Server server) {
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
