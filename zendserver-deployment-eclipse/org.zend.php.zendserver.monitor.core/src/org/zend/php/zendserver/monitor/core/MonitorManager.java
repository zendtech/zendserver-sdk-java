/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.zend.php.zendserver.monitor.internal.core.ZendServerMonitor;

/**
 * Represents management service for application monitoring.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorManager {

	private static Map<String, ZendServerMonitor> monitors;

	static {
		monitors = Collections
				.synchronizedMap(new HashMap<String, ZendServerMonitor>());
	}

	/**
	 * Create and start monitor for specified target and URL. If monitor already
	 * exists for that target, provided URL is added to be observed.
	 * 
	 * @param targetId
	 *            target id
	 * @param project
	 *            project name
	 * @param url
	 *            URL which will be observed
	 */
	public static void create(String targetId, String project, URL url) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor == null) {
			final ZendServerMonitor m = new ZendServerMonitor(targetId,
					project, url);
			m.start();
			monitors.put(targetId, m);
		} else {
			monitor.enable(url);
		}
	}

	/**
	 * Stop monitor for specified target id. If there is no monitor for that
	 * target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available and was cancelled;
	 *         otherwise return <code>false</code>
	 */
	public static boolean stop(String targetId) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null) {
			monitor.cancel();
			return true;
		}
		return false;
	}

	/**
	 * Start monitor for specified target id. If there is no monitor for that
	 * target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available and was started;
	 *         otherwise return <code>false</code>
	 */
	public static boolean start(String targetId) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null && monitor.getState() != Job.RUNNING
				&& monitor.getState() != Job.WAITING) {
			monitor.start();
			return true;
		}
		return false;
	}

	/**
	 * Remove monitoring of URL for specified target. If there is no monitor for
	 * that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @param url
	 *            URL
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean remove(String targetId, URL url) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null) {
			monitor.disable(url);
			if (!monitor.isEnabled()) {
				monitor.cancel();
				monitors.remove(targetId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove monitor for specified target. If there is no monitor for that
	 * target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @param url
	 *            URL
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean remove(String targetId) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null) {
			monitor.cancel();
			monitors.remove(targetId);
			return true;
		}
		return false;
	}

	/**
	 * Remove all available monitors for all targets.
	 */
	public static void removeAll() {
		Set<String> keys = monitors.keySet();
		for (String key : keys) {
			ZendServerMonitor monitor = monitors.get(key);
			monitor.cancel();
		}
	}

}
