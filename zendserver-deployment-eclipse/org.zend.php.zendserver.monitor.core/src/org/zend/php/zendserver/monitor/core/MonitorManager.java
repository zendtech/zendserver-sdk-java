/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.internal.core.AbstractMonitor;
import org.zend.php.zendserver.monitor.internal.core.TargetMonitor;
import org.zend.sdklib.target.IZendTarget;

/**
 * Represents management service for application monitoring.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
@SuppressWarnings("restriction")
public class MonitorManager {

	public static final String FILTERS_ATTRIBUTE = "monitoringFilters"; //$NON-NLS-1$
	public static final String HIDE_ATTRIBUTE = "monitoringHide"; //$NON-NLS-1$
	public static final String HIDE_TIME_ATTRIBUTE = "monitoringHideTime"; //$NON-NLS-1$
	public static final String SEVERITY_ATTRIBUTE = "monitoringSeverity"; //$NON-NLS-1$

	public static final String SLASH = "/"; //$NON-NLS-1$
	public static final String FILTER_SEPARATOR = ","; //$NON-NLS-1$
	public static final int DELAY_DEFAULT = 10;

	public static final int CODE_TRACE = 0x01;
	public static final int REPEAT = 0x10;

	private static final String HIDE_KEY = "hide"; //$NON-NLS-1$
	private static final String HIDE_TIME_KEY = "hide_time"; //$NON-NLS-1$
	private static final String FILTERS_PREF = "filters"; //$NON-NLS-1$

	private static Map<String, TargetMonitor> targetMonitors;

	static {
		targetMonitors = Collections
				.synchronizedMap(new HashMap<String, TargetMonitor>());
	}

	/**
	 * Create and start target monitor for specified target id.
	 * 
	 * @param targetId
	 *            target id
	 */
	public static boolean createTargetMonitor(String targetId) {
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor == null) {
			TargetMonitor m = new TargetMonitor(targetId);
			m.start();
			targetMonitors.put(targetId, m);
		}
		return true;
	}

	/**
	 * Remove concrete monitor.
	 * 
	 * @param monitor
	 */
	public static void removeMonitor(AbstractMonitor monitor) {
		Set<String> keys = targetMonitors.keySet();
		for (String key : keys) {
			TargetMonitor m = targetMonitors.get(key);
			if (m == monitor) {
				removeTargetMonitor(key);
				return;
			}
		}
	}

	/**
	 * Remove target monitor for specified target id. If there is no monitor for
	 * that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean removeTargetMonitor(String targetId) {
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor != null) {
			monitor.stop();
			targetMonitors.remove(targetId);
			return true;
		}
		return false;
	}

	/**
	 * Disable all target monitors.
	 * 
	 */
	public static void removeAllTargetMonitors() {
		Set<String> keys = targetMonitors.keySet();
		for (String key : keys) {
			TargetMonitor monitor = targetMonitors.get(key);
			monitor.stop();
			targetMonitors.remove(key);
		}
	}

	/**
	 * Update filters list for active target monitor.
	 * 
	 * @param targetId
	 */
	public static void updateFilters(String targetId) {
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor != null) {
			monitor.updateFilters();
		}
	}

	/**
	 * Check if monitor for particular target is started.
	 * 
	 * @param targetId
	 * @return <code>true</code> if monitor is started; otherwise return
	 *         <code>false</code>
	 */
	public static boolean isMonitorStarted(String targetId) {
		return targetMonitors.get(targetId) != null;
	}

	/**
	 * Returns list of filters for specified target.
	 * 
	 * @param targetId
	 * @return
	 */
	public static List<String> getFilters(String targetId) {
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor == null) {
			monitor = new TargetMonitor(targetId);
		}
		return monitor.getFilters();
	}

	public static boolean getHide(String targetId) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		String value = server.getAttribute(MonitorManager.HIDE_ATTRIBUTE, null);
		if (value == null) {
			IEclipsePreferences prefs = MonitorManager.getPreferences();
			prefs.get(HIDE_KEY, null);
		}
		return value != null ? Boolean.valueOf(value) : false;
	}

	public static int getHideTime(String targetId) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		String value = server.getAttribute(MonitorManager.HIDE_TIME_ATTRIBUTE,
				null);
		if (value == null) {
			IEclipsePreferences prefs = MonitorManager.getPreferences();
			prefs.get(HIDE_TIME_KEY, null);
		}
		return value != null ? Integer.valueOf(value) : DELAY_DEFAULT;
	}

	public static boolean getServerity(String targetId, String severityName) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		String value = server.getAttribute(MonitorManager.SEVERITY_ATTRIBUTE
				+ severityName, null);
		if (value == null) {
			IEclipsePreferences prefs = MonitorManager.getPreferences();
			prefs.get(targetId + '.' + severityName, null);
		}
		return value != null ? Boolean.valueOf(value) : true;
	}

	public static String getFiltersKey(String targetId) {
		return targetId + '.' + FILTERS_PREF;
	}

	public static IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
	}

	public static void addFilter(String targetId, String baseURL) {
		if (!baseURL.endsWith(SLASH)) {
			baseURL += SLASH;
		}
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor == null) {
			monitor = new TargetMonitor(targetId);
		}
		List<String> filters = monitor.getFilters();
		if (!filters.contains(baseURL)) {
			filters.add(baseURL);
		}
		if (!filters.isEmpty() && !isMonitorStarted(targetId)) {
			createTargetMonitor(targetId);
		}
		String newValue = monitor.getValue(filters);
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		if (server != null) {
			server.setAttribute(MonitorManager.FILTERS_ATTRIBUTE, newValue);
			ServersManager.save();
		}
		updateFilters(targetId);
	}

	public static void removeFilter(String targetId, String baseURL) {
		if (!baseURL.endsWith(SLASH)) {
			baseURL += SLASH;
		}
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor == null) {
			monitor = new TargetMonitor(targetId);
		}
		List<String> filters = monitor.getFilters();
		filters.remove(baseURL);
		if (filters.isEmpty() && isMonitorStarted(targetId)) {
			removeTargetMonitor(targetId);
		}
		String newValue = monitor.getValue(filters);
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		if (server != null) {
			server.setAttribute(MonitorManager.FILTERS_ATTRIBUTE, newValue);
			ServersManager.save();
		}
		updateFilters(targetId);
	}

}
