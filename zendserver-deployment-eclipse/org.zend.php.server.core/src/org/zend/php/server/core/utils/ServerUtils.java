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
package org.zend.php.server.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.server.internal.core.ServersCore;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Utility class for PHP Servers.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServerUtils {

	/**
	 * Setup project properties related to server settings for specified
	 * project.
	 * 
	 * @param project
	 * @param serverName
	 */
	public static void setupServerSettings(IProject project, String serverName) {
		Server server = ServersManager.getServer(serverName);
		if (server != null) {
			ServersManager.setDefaultServer(project, server);
			PHPProjectPreferences
					.setDefaultBasePath(project, project.getName());

			ProjectScope projectScope = new ProjectScope(project);
			IEclipsePreferences node = projectScope
					.getNode(IPHPDebugConstants.DEBUG_QUALIFIER);
			node.putBoolean(IPHPDebugConstants.DEBUG_PER_PROJECT, true);
			try {
				node.flush();
			} catch (BackingStoreException e) {
				ServersCore.logError(e);
			}
		}
	}

	/**
	 * Get target associated with server with specified name.
	 * 
	 * @param serverName
	 *            server name
	 * @return {@link IZendTarget} instance which is associated with specified
	 *         server name or <code>null</code> if such target does not exist
	 */
	public static IZendTarget getTarget(String serverName) {
		if (serverName != null) {
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (serverName.equals(target.getServerName())) {
					return target;
				}
			}
		}
		return null;
	}

	/**
	 * Get server associated with specified target.
	 * 
	 * @param target
	 * @return {@link Server} instance if there is a server associated with
	 *         specified target; otherwise return <code>null</code>
	 */
	public static Server getServer(IZendTarget target) {
		if (target != null) {
			String serverName = target.getServerName();
			if (serverName != null) {
				return ServersManager.getServer(serverName);
			}
		}
		return null;
	}

	/**
	 * Get target associated with specified server.
	 * 
	 * @param server
	 *            {@link Server} instance
	 * @return {@link IZendTarget} instance which is associated with specified
	 *         server or <code>null</code> if such target does not exist
	 */
	public static IZendTarget getTarget(Server server) {
		return server != null ? getTarget(server.getName()) : null;
	}

	/**
	 * Get target associated with specified project.
	 * 
	 * @param server
	 *            {@link IProject} instance
	 * @return {@link IZendTarget} instance which is associated with specified
	 *         project or <code>null</code> if such target does not exist
	 */
	public static IZendTarget getTarget(IProject project) {
		if (project != null) {
			Server server = ServersManager.getDefaultServer(project);
			return server != null ? getTarget(server.getName()) : null;
		}
		return null;
	}

}
