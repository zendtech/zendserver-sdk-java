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
package org.zend.php.server.ui.migration;

import java.net.MalformedURLException;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.ui.IStartup;

/**
 * Abstract implementation of migration service for PHP servers. It is intended
 * to extend this class to provide migration to different server types in
 * plug-in which provide these types. Implementors should be register on startup
 * using org.eclipse.ui.startup extension point.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractMigrationService implements IStartup {

	private static final String OLD_EMPTY_SERVER_NAME = "Default PHP Web Server"; //$NON-NLS-1$
	private static final String EMPTY_BASE_URL = "http://<no_php_server>"; //$NON-NLS-1$

	@Override
	public final void earlyStartup() {
		Server[] servers = ServersManager.getServers();
		boolean save = false;
		for (Server server : servers) {
			if (!ServersManager.isEmptyServer(server) && migrate(server)) {
				save = true;
			}
		}
		Server emptyServer = ServersManager
				.getServer(ServersManager.EMPTY_SERVER);
		if (emptyServer == null) {
			emptyServer = ServersManager.getServer(OLD_EMPTY_SERVER_NAME);
		}
		if (emptyServer == null) {
			try {
				emptyServer = new Server(ServersManager.DEFAULT_SERVER_NAME,
						"localhost", EMPTY_BASE_URL, ""); //$NON-NLS-1$ //$NON-NLS-2$
				emptyServer.setAttribute(ServersManager.EMPTY_SERVER,
						String.valueOf(true));
				emptyServer = ServersManager.getServer(emptyServer);
				ServersManager.addServer(emptyServer);
				save = true;
			} catch (MalformedURLException e) {
				// safe server creation
			}
		}
		if (save) {
			ServersManager.save();
		}
	}

	/**
	 * Perform migration of PHP server. Implementors can perform any operations
	 * as a part of migration process but in order to performance time consuming
	 * operations should be avoided.
	 * 
	 * @return <code>true</code> if server was migrated; otherwise return
	 *         <code>false</code>
	 */
	/**
	 * @param server
	 * @return
	 */
	protected abstract boolean migrate(Server server);

	protected String getServerType(Server server) {
		return server.getAttribute(IServerType.TYPE, null);
	}

	protected void setType(Server server, String id) {
		server.setAttribute(IServerType.TYPE, id);
	}

}
