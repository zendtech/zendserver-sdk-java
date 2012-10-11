package org.zend.php.zendserver.deployment.debug.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.application.ZendDebugMode;
import org.zend.sdklib.application.ZendDebugMode.State;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class DebugModeManager {

	private static final String DEBUG_STOP = "debug_stop"; //$NON-NLS-1$

	private static final String DEBUG_HOST = "debug_host"; //$NON-NLS-1$

	private static final String DEBUG_PORT = "debug_port"; //$NON-NLS-1$

	private static final String LOCALHOST = "localhost"; //$NON-NLS-1$

	public static final String DEBUG_MODE_NODE = Activator.PLUGIN_ID
			+ "/debugMode"; //$NON-NLS-1$

	public static final String FILTER_SEPARATOR = ","; //$NON-NLS-1$

	private static final String ZENDSERVER_PORT_KEY = "zendserver_default_port"; //$NON-NLS-1$
	private static final String CLIENT_HOST_KEY = "org.eclipse.php.debug.coreclient_ip"; //$NON-NLS-1$
	private static final String DEBUG_PLUGIN_ID = "org.eclipse.php.debug.core"; //$NON-NLS-1$

	private static DebugModeManager manager;

	private Map<IZendTarget, Boolean> targets;

	private DebugModeManager() {
		this.targets = new HashMap<IZendTarget, Boolean>();
	}

	public static DebugModeManager getManager() {
		if (manager == null) {
			manager = new DebugModeManager();
		}
		return manager;
	}

	@SuppressWarnings("restriction")
	public IStatus startDebugMode(IZendTarget target) {
		ZendDebugMode debugMode = new ZendDebugMode(target.getId());
		Map<String, String> options = new HashMap<String, String>();
		debugMode.setFilters(getFilters(target));
		AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry
				.getDebuggerConfiguration(DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID);
		if (debuggerConfiguration != null) {
			int port = debuggerConfiguration.getPort();
			options.put(DEBUG_PORT, String.valueOf(port));
			options.put(DEBUG_HOST, getDebugHosts(target));
			options.put(DEBUG_STOP, "1"); //$NON-NLS-1$
			options.put("use_remote", "1");
			debugMode.setOptions(options);
		}
		State result = State.ERROR;
		try {
			result = debugMode.start();
		} catch (SdkException e) {
			Activator.log(e);
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause()
					.getMessage());
		}
		if (result == State.STARTING) {
			targets.put(target, true);
			return new Status(IStatus.OK, Activator.PLUGIN_ID,
					Messages.DebugModeManager_StartSuccess);
		}
		if (result == State.STARTED) {
			return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					Messages.DebugModeManager_AlreadyStartedWarning);
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.DebugModeManager_CannotStartError);
	}

	public IStatus stopDebugMode(IZendTarget target) {
		ZendDebugMode debugMode = new ZendDebugMode(target.getId());
		State result = State.ERROR;
		try {
			result = debugMode.stop();
		} catch (SdkException e) {
			Activator.log(e);
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause()
					.getMessage());
		}
		if (result == State.STOPPING) {
			targets.put(target, false);
			return new Status(IStatus.OK, Activator.PLUGIN_ID,
					Messages.DebugModeManager_StopSuccess);
		}
		if (result == State.STOPPED) {
			return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					Messages.DebugModeManager_AlreadyStoppedWarning);
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.DebugModeManager_CannotStopError);
	}

	public boolean isInDebugMode(IZendTarget target) {
		Boolean value = targets.get(target);
		if (value == null || !value) {
			return false;
		}
		return true;
	}

	public static void stopAll() {
		DebugModeManager manager = getManager();
		Set<IZendTarget> keys = manager.targets.keySet();
		for (IZendTarget target : keys) {
			if (manager.isInDebugMode(target)) {
				manager.stopDebugMode(target);
			}
		}
	}
	
	@SuppressWarnings("restriction")
	public static Server findExistingServer(IZendTarget target) {
		try {
			URL baseURL = target.getDefaultServerURL();
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())) {
					if ((serverBaseURL.getPort() == baseURL.getPort())
							|| (isDefaultPort(serverBaseURL) && isDefaultPort(baseURL))) {
						return server;
					} else {
						String zsPort = server.getAttribute(
								ZENDSERVER_PORT_KEY, "-1"); //$NON-NLS-1$
						if (Integer.valueOf(zsPort) == baseURL.getPort()) {
							return server;
						}
					}
				}
			}
			return createPHPServer(target.getDefaultServerURL(), target.getId());
		} catch (MalformedURLException e) {
			Activator.log(e);
			// do nothing and return null
		}
		return null;
	}

	protected static boolean isDefaultPort(URL url) {
		int port = url.getPort();
		if (port == -1 || port == 80) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("restriction")
	protected static Server createPHPServer(URL baseURL, String targetId) {
		try {
			URL url = new URL(baseURL.getProtocol(), baseURL.getHost(),
					baseURL.getPort(), ""); //$NON-NLS-1$
			String urlString = url.toString();
			Server server = new Server(
					"Zend Target (id: " + targetId + " host: " + url.getHost() //$NON-NLS-1$ //$NON-NLS-2$
							+ ")", urlString, urlString, ""); //$NON-NLS-1$ //$NON-NLS-2$
			ServersManager.addServer(server);
			ServersManager.save();
			return server;
		} catch (MalformedURLException e) {
			Activator.log(e);
			// ignore, verified earlier
		}
		return null;
	}

	private String getDebugHosts(IZendTarget target) {
		if (TargetsManager.isOpenShift(target)
				|| TargetsManager.isPhpcloud(target)) {
			return LOCALHOST;
		}
		String host = target.getHost().getHost();
		if (host.equals(LOCALHOST)) {
			return LOCALHOST;
		}
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(DEBUG_PLUGIN_ID);
		String clientHosts = prefs.get(CLIENT_HOST_KEY, (String) null);
		if (clientHosts == null) {
			IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE
					.getNode(DEBUG_PLUGIN_ID);
			clientHosts = defaultPrefs.get(CLIENT_HOST_KEY, (String) null);
		}
		return clientHosts;
	}

	private String[] getFilters(IZendTarget target) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(DEBUG_MODE_NODE);
		IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE
				.getNode(DEBUG_MODE_NODE);
		String val = prefs.get(target.getId(),
				defaultPrefs.get(target.getId(), target.getHost().toString()));
		return val.split(FILTER_SEPARATOR);
	}

}
