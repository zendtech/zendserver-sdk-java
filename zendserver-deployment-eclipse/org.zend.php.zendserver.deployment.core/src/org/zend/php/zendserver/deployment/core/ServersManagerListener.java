/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.IServersManagerListener;
import org.eclipse.php.internal.server.core.manager.ServerManagerEvent;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Implementation of {@link IServersManagerListener}. It is added during plug-in
 * initialization to support changes in servers settings which should affect
 * targets configuration.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersManagerListener implements IServersManagerListener {

	@Override
	public void serverRemoved(ServerManagerEvent event) {
		Server removedServer = event.getServer();
		if (removedServer != null
				&& ServersManager.getServer(removedServer.getName()) == null) {
			String name = removedServer.getName();
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			IZendTarget[] targets = manager.getTargets();
			IZendTarget toRemove = null;
			for (IZendTarget target : targets) {
				if (name.equals(target.getServerName())) {
					toRemove = target;
					break;
				}
			}
			if (toRemove != null) {
				manager.remove(toRemove);
			}
		}
	}

	@Override
	public void serverModified(ServerManagerEvent event) {
		String key = event.getModifiedAttributeKey();
		if (Server.NAME.equals(key)) {
			String oldValue = event.getOldAttributeValue();
			String newValue = event.getNewAttributeValue();
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (oldValue.equals(target.getServerName())) {
					ZendTarget t = (ZendTarget) target;
					t.setServerName(newValue);
					manager.updateTarget(t, true);
				}
			}
		}
	}

	@Override
	public void serverAdded(ServerManagerEvent event) {
	}

}
