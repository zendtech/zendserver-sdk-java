/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.internal.database;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
@SuppressWarnings("restriction")
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

	protected IStatus result;

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
	 * @param target
	 *            zend target
	 * @param databaseManager
	 *            database manager
	 * @return instance of corresponding target database
	 */
	public static ITargetDatabase create(IZendTarget target,
			TargetsDatabaseManager databaseManager) {
		if (TargetsManager.isOpenShift(target)) {
			return new OpenShiftDatabase(target, databaseManager);
		}
		return null;
	}

	@Override
	public boolean createProfile() {
		result = Status.OK_STATUS;
		boolean isValid = isTunnelAvailable();
		if (!isValid) {
			isValid = connectTunnel();
		}
		if (isValid) {
			String url = getUrl();
			profile = findProfile();
			if (profile != null) {
				validatePort();
				initListener();
				return true;
			}
			DriverInstance driver = getMySQLDriver();
			Properties properties = null;
			if (driver != null) {
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
				String password = getPassword();
				if (password != null) {
					properties.setProperty(
							IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID,
							password);
				}
				properties.setProperty(
						IJDBCDriverDefinitionConstants.URL_PROP_ID, url);
			}
			if (properties == null) {
				result = new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID,
						Messages.TargetDatabase_NoDriversError);
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
				result = new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID,
						e.getMessage());
				DeploymentCore.log(e);
			}
		}
		return false;
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public void setUsername(String username) {
		Properties props = profile.getBaseProperties();
		if (username != null) {
			props.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID,
					username);
		} else {
			props.remove(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
		}
		profile.setBaseProperties(props);
	}

	@Override
	public ConnectionState getState() {
		if (profile != null) {
			return ConnectionState.byState(profile.getConnectionState());
		}
		return ConnectionState.UNAVAILABLE;
	}

	@Override
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

	@Override
	public boolean isSavePassword() {
		return savePassword;
	}

	@Override
	public void setSavePassword(boolean save) {
		this.savePassword = save;
	}

	@Override
	public void remove() {
		profile = null;
		manager.stateChanged(this, ConnectionState.UNAVAILABLE);
	}

	@Override
	public IZendTarget getTarget() {
		return target;
	}

	@Override
	public IStatus getResult() {
		return result;
	}

	protected abstract String getUrl();

	protected abstract String getUsername();

	protected abstract String getPassword();

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

	private DriverInstance getMySQLDriver() {
		DriverInstance[] drivers = DriverManager.getInstance()
				.getAllDriverInstances();
		double resultVersion = 0;
		DriverInstance result = null;
		for (DriverInstance driverInstance : drivers) {
			String name = driverInstance.getName();
			if (name.contains("MySQL")) { //$NON-NLS-1$
				Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))"); //$NON-NLS-1$
				Matcher m = p.matcher(name);
				if (m.find()) {
					double version = Double.parseDouble(m.group(1));
					if (version > resultVersion) {
						result = driverInstance;
						resultVersion = version;
					}
				}
			}
		}
		return result;
	}

}
