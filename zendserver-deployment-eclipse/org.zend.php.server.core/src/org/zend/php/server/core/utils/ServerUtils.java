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

}