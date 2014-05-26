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
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.monitor.internal.core.AbstractMonitor;
import org.zend.php.zendserver.monitor.internal.core.TargetMonitor;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Represents management service for application monitoring.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorManager {

	public static final String SLASH = "/"; //$NON-NLS-1$
	//public static final String ENABLED = "enabled."; //$NON-NLS-1$
	//public static final String ENABLED_ALL = "enabledAll"; //$NON-NLS-1$
	private static final String HIDE_KEY = "hide"; //$NON-NLS-1$
	private static final String HIDE_TIME_KEY = "hide_time"; //$NON-NLS-1$
	private static final String FILTERS_PREF = "filters"; //$NON-NLS-1$
	public static final String FILTER_SEPARATOR = ","; //$NON-NLS-1$
	public static final int DELAY_DEFAULT = 10;

	public static final int CODE_TRACE = 0x01;
	public static final int REPEAT = 0x10;

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
			if (m.start()) {
				targetMonitors.put(targetId, m);
			} else {
				return false;
			}
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
			monitor.disable(true);
			if (!monitor.isEnabled()) {
				monitor.cancel();
				try {
					monitor.flushPreferences();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
				targetMonitors.remove(targetId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Disable all target monitors.
	 * 
	 * @throws BackingStoreException
	 */
	public static void removeAllTargetMonitors() throws BackingStoreException {
		Set<String> keys = targetMonitors.keySet();
		for (String key : keys) {
			TargetMonitor monitor = targetMonitors.get(key);
			monitor.cancel();
			monitor.flushPreferences();
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
	 * 
	 * Set preference value for specified target.
	 * 
	 * @param targetId
	 * @param enable
	 */
	public static void setTargetEnabled(String targetId, boolean enable) {
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor != null) {
			monitor.setEnabled(enable);
		} else {
			monitor = new TargetMonitor(targetId);
			monitor.setEnabled(enable);
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
		return Activator.getDefault().getPreferenceStore().getBoolean(targetId);
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
		return monitor.getFilters(targetId);
	}

	public static String getHideKey(String targetId) {
		return targetId + '.' + HIDE_KEY;
	}

	public static String getHideTimeKey(String targetId) {
		return targetId + '.' + HIDE_TIME_KEY;
	}

	public static String getFiltersKey(String targetId) {
		return targetId + '.' + FILTERS_PREF;
	}
	
	public static IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
	}

	public static void setDefaultPreferences(IZendTarget target) {
		String id = target.getId();
		IPreferenceStore preferences = Activator.getDefault()
				.getPreferenceStore();
		preferences.setDefault(MonitorManager.getFiltersKey(id), ""); //$NON-NLS-1$
		preferences.setDefault(MonitorManager.getHideKey(id), false);
		preferences.setDefault(MonitorManager.getHideTimeKey(id),
				MonitorManager.DELAY_DEFAULT);
		IssueSeverity[] severityValues = IssueSeverity.values();
		for (IssueSeverity sev : severityValues) {
			preferences.setDefault(id + '.' + sev.getName(), true);
		}
	}

	public static void removePreferences(IZendTarget target) {
		String id = target.getId();
		IPreferenceStore preferences = Activator.getDefault()
				.getPreferenceStore();
		preferences.setToDefault(MonitorManager.getFiltersKey(id));
		preferences.setToDefault(MonitorManager.getHideKey(id));
		preferences.setToDefault(MonitorManager.getHideTimeKey(id));
		IssueSeverity[] severityValues = IssueSeverity.values();
		for (IssueSeverity sev : severityValues) {
			preferences.setToDefault(id + '.' + sev.getName());
		}
	}

	public static void addFilter(String targetId, String baseURL) {
		if (!baseURL.endsWith(SLASH)) { 
			baseURL += SLASH; 
		}
		TargetMonitor monitor = targetMonitors.get(targetId);
		if (monitor == null) {
			monitor = new TargetMonitor(targetId);
		}
		List<String> filters = monitor.getFilters(targetId);
		if (!filters.contains(baseURL)) {
			filters.add(baseURL);
		}
		if (!filters.isEmpty() && !isMonitorStarted(targetId)) {
			setTargetEnabled(targetId, true);
			createTargetMonitor(targetId);
		}
		String newValue = monitor.getValue(filters);
		IPreferenceStore preferences = Activator.getDefault()
				.getPreferenceStore();
		preferences.setValue(MonitorManager.getFiltersKey(targetId), newValue);
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
		List<String> filters = monitor.getFilters(targetId);
		filters.remove(baseURL);
		if (filters.isEmpty() && isMonitorStarted(targetId)) {
			setTargetEnabled(targetId, false);
			removeTargetMonitor(targetId);
		}
		String newValue = monitor.getValue(filters);
		IPreferenceStore preferences = Activator.getDefault()
				.getPreferenceStore();
		preferences.setValue(MonitorManager.getFiltersKey(targetId), newValue);
		updateFilters(targetId);
	}

}
