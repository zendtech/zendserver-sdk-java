package org.zend.php.zendserver.deployment.debug.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsUtil;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.application.ZendDebugMode;
import org.zend.sdklib.application.ZendDebugMode.State;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

@SuppressWarnings("restriction")
public class DebugModeManager {

	public static final int[] prohibitedPorts = new int[] { 10081, 10082 };

	public static final String SERVER_ATTRIBUTE = "debugModeFilters"; //$NON-NLS-1$

	private static final String DEBUG_STOP = "debug_stop"; //$NON-NLS-1$

	private static final String DEBUG_HOST = "debug_host"; //$NON-NLS-1$

	private static final String DEBUG_PORT = "debug_port"; //$NON-NLS-1$

	private static final String LOCALHOST = "localhost"; //$NON-NLS-1$

	public static final String DEBUG_MODE_NODE = Activator.PLUGIN_ID
			+ "/debugMode"; //$NON-NLS-1$

	public static final String FILTER_SEPARATOR = ","; //$NON-NLS-1$

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

	public IStatus startDebugMode(IZendTarget target) {
		ZendDebugMode debugMode = new ZendDebugMode(target.getId());
		Map<String, String> options = new HashMap<String, String>();
		debugMode.setFilters(getFilters(target));
		options.put(DEBUG_PORT, String.valueOf(getDebugPort(target)));
		options.put(DEBUG_HOST, getDebugHosts(target));
		Server server = ServerUtils.getServer(target);
		if (server != null) {
			if (shouldStopAtFirstLine()) {
				options.put(DEBUG_STOP, "1"); //$NON-NLS-1$
			}
		} else {
			options.put(DEBUG_STOP, "1"); //$NON-NLS-1$
		}
		debugMode.setOptions(options);
		State result = State.ERROR;
		try {
			result = debugMode.start();
		} catch (SdkException e) {
			Activator.log(e);
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
			if (isInDebugMode(target)) {
				targets.put(target, false);
			}
			return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					Messages.DebugModeManager_AlreadyStoppedWarning);
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.DebugModeManager_CannotStopError);
	}

	public IStatus restartDebugMode(IZendTarget target) {
		IStatus stopStatus = stopDebugMode(target);
		if (stopStatus.getSeverity() != IStatus.ERROR) {
			return startDebugMode(target);
		} else {
			return stopStatus;
		}
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

	private String getDebugHosts(IZendTarget target) {
		String host = target.getHost().getHost();
		if (host.equals(LOCALHOST)) {
			return LOCALHOST;
		}
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(PHPDebugPlugin.ID);
		String clientHosts = prefs.get(PHPDebugCorePreferenceNames.CLIENT_IP,
				(String) null);
		if (clientHosts == null) {
			IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE
					.getNode(PHPDebugPlugin.ID);
			clientHosts = defaultPrefs.get(
					PHPDebugCorePreferenceNames.CLIENT_IP, (String) null);
		}
		Server server = ServerUtils.getServer(target);
		// Get server individual hosts list if any
		String customHosts = ZendDebuggerSettingsUtil.getDebugHosts(server.getUniqueId());
		if (customHosts != null)
			clientHosts = customHosts;
		return clientHosts;
	}

	private int getDebugPort(IZendTarget target) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(PHPDebugPlugin.ID);
		int clientHosts = prefs.getInt(
				PHPDebugCorePreferenceNames.ZEND_DEBUG_PORT, -1);
		if (clientHosts == -1) {
			IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE
					.getNode(PHPDebugPlugin.ID);
			clientHosts = defaultPrefs.getInt(
					PHPDebugCorePreferenceNames.ZEND_DEBUG_PORT, -1);
		}
		Server server = ServerUtils.getServer(target);
		// Get server individual hosts list if any
		int customPort = ZendDebuggerSettingsUtil.getDebugPort(server.getUniqueId());
		if (customPort != -1)
			clientHosts = customPort;
		return clientHosts;
	}

	/**
	 * Get Debug Mode filters. Firstly, it tries to read them from PHP server
	 * configuration. If they are not defined in it, then reads them from
	 * preferences.
	 * 
	 * @param target
	 * @return list of Debug Mode filters; it may be empty
	 */
	private String[] getFilters(IZendTarget target) {
		Server server = ServerUtils.getServer(target);
		String value = server.getAttribute(SERVER_ATTRIBUTE, null);
		if (value == null) {
			IEclipsePreferences prefs = InstanceScope.INSTANCE
					.getNode(DEBUG_MODE_NODE);
			value = prefs.get(target.getId(), null);
		}
		List<String> filters = null;
		if (value != null && value.length() > 0) {
			filters = new ArrayList<String>(Arrays.asList(value
					.split(FILTER_SEPARATOR)));
		} else {
			filters = new ArrayList<String>();
		}
		return filters.toArray(new String[0]);
	}

	private boolean shouldStopAtFirstLine() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(PHPDebugPlugin.ID);
		return prefs.getBoolean(PHPDebugCorePreferenceNames.STOP_AT_FIRST_LINE,
				true);
	}

}
