/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.internal.database;

import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.datatools.connectivity.ConnectEvent;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.IManagedConnectionListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.database.ConnectionState;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;
import org.zend.php.zendserver.deployment.core.database.TargetsDatabaseManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnel.State;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
public abstract class TargetDatabase implements ITargetDatabase {

	protected static final String PROTOCOL = "jdbc:mysql://"; //$NON-NLS-1$
	protected static final String DEFAULT_HOST = "localhost"; //$NON-NLS-1$
	protected static final String PROVIDER_ID = "org.eclipse.datatools.enablement.mysql.connectionProfile"; //$NON-NLS-1$

	protected IZendTarget target;
	protected String profileId;

	protected String password;
	protected boolean savePassword;

	protected TargetsDatabaseManager manager;
	protected IConnectionProfile profile;

	protected TargetDatabase(IZendTarget target, TargetsDatabaseManager manager) {
		super();
		this.target = target;
		this.manager = manager;
		init();
	}

	/**
	 * Create instance of target database that corresponds to specified target's
	 * type.
	 * 
	 * @param t
	 *            zend target
	 * @param m
	 *            database manager
	 * @return instance of corresponding target database
	 */
	public static ITargetDatabase create(IZendTarget t, TargetsDatabaseManager m) {
		if (TargetsManager.isOpenShift(t)) {
			return new OpenShiftDatabase(t, m);
		}
		if (TargetsManager.isPhpcloud(t)) {
			return new PhpcloudDatabase(t, m);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.core.database.ITargetDatabase#
	 * createProfile()
	 */
	public boolean createProfile() {
		boolean isValid = isTunnelAvailable();
		if (!isValid) {
			isValid = connectTunnel();
		}
		if (isValid) {
			String url = getUrl();
			if (url == null) {
				// TODO handle null url
				return false;
			}
			profile = findProfile();
			if (profile != null) {
				validatePort();
				initListener();
				return true;
			}

			DriverInstance[] dilist = DriverManager.getInstance()
					.getAllDriverInstances();
			Properties properties = null;
			if (dilist.length > 0) {
				DriverInstance driver = dilist[0];
				IPropertySet propSet = driver.getPropertySet();
				properties = propSet.getBaseProperties();
				properties.setProperty(TARGET_ID, target.getId());
				properties.setProperty(
						ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID,
						propSet.getID());
				properties.setProperty(
						IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID,
						getDatabaseName());
				properties.setProperty(
						IJDBCDriverDefinitionConstants.USERNAME_PROP_ID,
						getUsername());
				properties.setProperty(
						IJDBCDriverDefinitionConstants.URL_PROP_ID, url);
			}
			if (properties == null) {
				// TODO handle it
				return false;
			}
			try {
				String profileName = getProfilePrefix() + ": " //$NON-NLS-1$
						+ target.getHost().getHost();
				String description = Messages.TargetDatabase_1 + target.getId();
				profile = ProfileManager.getInstance().getProfileByName(
						profileName);
				if (profile == null) {
					profile = ProfileManager.getInstance().createProfile(
							profileName, description, PROVIDER_ID, properties,
							"", false); //$NON-NLS-1$
				} else {
					// be sure that port is valid
					validatePort();
				}
				initListener();
				return true;
			} catch (ConnectionProfileException e) {
				DeploymentCore.log(e);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.ITargetDatabase
	 * #connect(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean connect(IProgressMonitor monitor) {
		if (connectTunnel()) {
			if (profile != null) {
				profileId = profile.getInstanceID();
				IStatus status = profile.connect();
				if (status.getSeverity() == IStatus.OK) {
					savePassword();
					manager.stateChanged(this, ConnectionState.CONNECTED);
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.ITargetDatabase
	 * #disconnect()
	 */
	public void disconnect() {
		IConnectionProfile profile = ProfileManager.getInstance()
				.getProfileByInstanceID(profileId);
		if (profile != null) {
			profile.disconnect(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().getSeverity() == IStatus.OK) {
						manager.stateChanged(TargetDatabase.this,
								ConnectionState.DISCONNECTED);
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.database.ITargetDatabase#setPassword
	 * (java.lang.String)
	 */
	public void setPassword(String password) {
		Properties props = profile.getBaseProperties();
		if (password != null) {
			props.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID,
					password);
			this.password = password;
		} else {
			props.remove(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
			props.remove(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID);
		}
		profile.setBaseProperties(props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.internal.database.ITargetDatabase
	 * #isConnected()
	 */
	public ConnectionState getState() {
		if (profile != null) {
			return ConnectionState.byState(profile.getConnectionState());
		}
		return ConnectionState.UNAVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.database.ITargetDatabase#hasPassword
	 * ()
	 */
	public boolean hasPassword() {
		if (profile != null) {
			String password = profile.getBaseProperties().getProperty(
					IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
			if (password != null && password.length() > 0) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.core.database.ITargetDatabase#
	 * isSavePassword()
	 */
	public boolean isSavePassword() {
		return savePassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.core.database.ITargetDatabase#
	 * setSavePassword(boolean)
	 */
	public void setSavePassword(boolean save) {
		this.savePassword = save;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.database.ITargetDatabase#remove()
	 */
	public void remove() {
		profile = null;
		manager.stateChanged(this, ConnectionState.UNAVAILABLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.database.ITargetDatabase#getTarget
	 * ()
	 */
	public IZendTarget getTarget() {
		return target;
	}

	protected abstract String getUrl();

	protected abstract String getUsername();

	protected abstract String getDatabaseName();

	protected abstract String getProfilePrefix();

	private boolean connectTunnel() {
		try {
			String serverName = target.getServerName();
			Server server = ServersManager.getServer(serverName);
			SSHTunnelConfiguration config = SSHTunnelConfiguration.read(server);
			State result = SSHTunnelManager.getManager().connect(config);
			switch (result) {
			case CONNECTING:
			case CONNECTED:
				return true;
			case NOT_SUPPORTED:
				return false;
			}
		} catch (Exception e) {
			DeploymentCore.log(e);
		}
		return false;
	}

	private void init() {
		profile = findProfile();
		if (profile != null) {
			initListener();
		}
	}

	private void initListener() {
		IManagedConnection managedConnetion = profile
				.getManagedConnection("java.sql.Connection"); //$NON-NLS-1$
		if (managedConnetion != null) {
			managedConnetion
					.addConnectionListener(new IManagedConnectionListener() {

						public void opened(ConnectEvent event) {
							manager.stateChanged(TargetDatabase.this,
									ConnectionState.CONNECTED);
						}

						public void modified(ConnectEvent event) {
							// TODO handle port change - maybe reconnect to ssh
							// tunnel using new port value
						}

						public boolean okToClose(ConnectEvent event) {
							return true;
						}

						public void aboutToClose(ConnectEvent event) {
						}

						public void closed(ConnectEvent event) {
							manager.stateChanged(TargetDatabase.this,
									ConnectionState.DISCONNECTED);
						}

					});
		}
	}

	private IConnectionProfile findProfile() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfiles();
		for (IConnectionProfile profile : profiles) {
			Properties properties = profile.getBaseProperties();
			String targetId = properties.getProperty(TARGET_ID);
			if (target.getId().equals(targetId)) {
				return profile;
			}
		}
		return null;
	}

	private boolean isTunnelAvailable() {
		String serverName = target.getServerName();
		Server server = ServersManager.getServer(serverName);
		return SSHTunnelManager.getManager().isConnected(server.getHost());
	}

	private void validatePort() {
		String serverName = target.getServerName();
		Server server = ServersManager.getServer(serverName);
		int port = SSHTunnelManager.getManager().getDatabasePort(
				server.getHost());
		Properties properties = profile.getBaseProperties();
		String url = properties
				.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
		if (url != null) {
			url = url.substring(PROTOCOL.length());
			int index = url.indexOf('/');
			if (index != -1) {
				String[] segments = url.substring(0, index).split(":"); //$NON-NLS-1$
				if (segments.length == 2) {
					int currentPort = Integer.valueOf(segments[1]);
					if (currentPort != port) {
						properties.setProperty(
								IJDBCDriverDefinitionConstants.URL_PROP_ID,
								getUrl());
						profile.setBaseProperties(properties);
					}
				}
			}
		}

	}

	private void savePassword() {
		Properties props = profile.getBaseProperties();
		if (savePassword) {
			TargetsManagerService.INSTANCE.storeContainerPassword(target,
					password);
			props.setProperty(
					IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID,
					String.valueOf(savePassword));
		}
		profile.setBaseProperties(props);
	}

}
