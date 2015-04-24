/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.zend.php.zendserver.deployment.core.internal.database.TargetDatabase;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link ITargetDatabase} manager.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class TargetsDatabaseManager {

	private static TargetsDatabaseManager manager;

	private Map<IZendTarget, ITargetDatabase> connections;
	private List<ITargetDatabaseListener> listeners;

	private TargetsDatabaseManager() {
		this.connections = new HashMap<IZendTarget, ITargetDatabase>();
		this.listeners = new ArrayList<ITargetDatabaseListener>();
	}

	/**
	 * @return {@link TargetsDatabaseManager} instance
	 */
	public static TargetsDatabaseManager getManager() {
		if (manager == null) {
			manager = new TargetsDatabaseManager();
			manager.initialize();
		}
		return manager;
	}

	/**
	 * Create {@link ITargetDatabase} instance for specified phpcloud container.
	 * 
	 * @param target
	 *            phpcloud container
	 * @return TargetDatabase instance
	 */
	public ITargetDatabase getConnection(IZendTarget target) {
		ITargetDatabase connection = null;
		Set<IZendTarget> targetsSet = connections.keySet();
		for (IZendTarget t : targetsSet) {
			if (target.getHost().getHost().equals(t.getHost().getHost())) {
				connection = connections.get(t);
				break;
			}
		}
		if (connection == null) {
			connection = TargetDatabase.create(target, this);
			connections.put(target, connection);
		}
		return connection;
	}

	/**
	 * Connect to the database of specified phpcloud container.
	 * 
	 * @param target
	 *            phpcloud container
	 * @param monitor
	 *            progress monitor
	 * @return <code>true</code> if connected successfully; otherwise return
	 *         <code>false</code>
	 * @throws IOException
	 */
	public boolean connect(IZendTarget target, IProgressMonitor monitor)
			throws IOException {
		ITargetDatabase connection = getConnection(target);
		return openConnection(connection, monitor);
	}

	/**
	 * Disconnect database connection for specified phpcloud container.
	 * 
	 * @param target
	 *            phpcloud container
	 */
	public void disconnect(IZendTarget target) {
		ITargetDatabase connection = connections.get(target);
		if (connection != null) {
			connection.disconnect();
		}
	}

	/**
	 * Propagate connection state change of spacified {@link ITargetDatabase}.
	 * 
	 * @param targetDatabase
	 *            {@link ITargetDatabase} which changed its state
	 * @param state
	 *            new state
	 */
	public void stateChanged(ITargetDatabase targetDatabase,
			ConnectionState state) {
		for (ITargetDatabaseListener listener : listeners) {
			listener.stateChanged(targetDatabase, state);
		}
	}

	/**
	 * Disconnect all database connections for phpcloud containers.
	 */
	public void disconnectAll() {
		Set<IZendTarget> targetsSet = connections.keySet();
		for (IZendTarget target : targetsSet) {
			disconnect(target);
		}
	}

	/**
	 * Add target database listener.
	 * 
	 * @param listener
	 */
	public void addTargetDatabaseListener(ITargetDatabaseListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove target database listener.
	 * 
	 * @param listener
	 */
	public void removeTargetDatabaseListener(ITargetDatabaseListener listener) {
		listeners.add(listener);
	}

	private void initialize() {
		ProfileManager.getInstance().addProfileListener(new IProfileListener() {

			public void profileDeleted(IConnectionProfile profile) {
				String targetId = profile.getBaseProperties().getProperty(
						ITargetDatabase.TARGET_ID);
				Set<IZendTarget> targetsSet = connections.keySet();
				for (IZendTarget t : targetsSet) {
					if (t.getId().equals(targetId)) {
						connections.get(t).remove();
					}
				}
			}

			public void profileChanged(IConnectionProfile profile) {
			}

			public void profileAdded(IConnectionProfile profile) {
			}
		});
	}

	private boolean openConnection(ITargetDatabase connection,
			IProgressMonitor monitor) {
		return connection.connect(monitor);
	}

}
