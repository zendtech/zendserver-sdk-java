/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.monitor.internal.core.Startup;
import org.zend.php.zendserver.monitor.internal.core.ZendServerMonitor;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Represents management service for application monitoring.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorManager {

	public static final String ENABLED = "enabled."; //$NON-NLS-1$
	public static final String HIDE_KEY = "hide"; //$NON-NLS-1$
	public static final String HIDE_TIME_KEY = "hide_time"; //$NON-NLS-1$

	private static Map<String, ZendServerMonitor> monitors;

	static {
		monitors = Collections
				.synchronizedMap(new HashMap<String, ZendServerMonitor>());
	}

	/**
	 * Enable monitor for specified target and URL. If monitor already exists
	 * for that target, provided URL is added to be observed.
	 * 
	 * @param targetId
	 *            target id
	 * @param project
	 *            project name
	 * @param url
	 *            URL which will be observed
	 */
	public static boolean create(String targetId, String project, URL url) {
		try {
			ZendServerMonitor monitor = monitors.get(targetId);
			if (monitor == null) {
				ZendServerMonitor m = new ZendServerMonitor(targetId, project,
						url);
				if (m.start()) {
					monitors.put(targetId, m);
					return true;
				}
			} else {
				monitor.enable(project, url);
				return true;
			}
		} catch (Exception e) {
			Activator.log(e);
			// revert changes
			ZendServerMonitor m = new ZendServerMonitor(targetId, project, url);
			m.setEnabled(project, false);
		}
		return false;
	}

	/**
	 * Create and start monitor for specified project. Monitor will be created
	 * only on those targets where application is deployed and launch
	 * configuration is available.
	 * 
	 * @param projectName
	 *            project name
	 */
	public static void create(String projectName) {
		List<ILaunchConfiguration> launches = getLaunches(projectName);
		for (ILaunchConfiguration config : launches) {
			try {
				String targetId = config.getAttribute(Startup.TARGET_ID,
						(String) null);
				String baseURL = config.getAttribute(Startup.BASE_URL,
						(String) null);
				if (targetId != null && baseURL != null) {
					MonitorManager.create(targetId, projectName, new URL(
							baseURL));
				}
			} catch (CoreException e) {
				Activator.log(e);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
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
	 * @param projectName
	 *            project name
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean remove(String targetId, String projectName) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null) {
			monitor.disable(projectName);
			if (!monitor.isEnabled()) {
				monitor.cancel();
				try {
					monitor.flushPreferences();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
				monitors.remove(targetId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove monitoring of specified project from all available monitors.
	 * 
	 * @param projectName
	 *            project name
	 */
	public static void removeProject(String projectName) {
		Set<String> targetsSet = monitors.keySet();
		List<String> toRemove = new ArrayList<String>();
		for (String targetId : targetsSet) {
			ZendServerMonitor monitor = monitors.get(targetId);
			if (monitor != null) {
				monitor.disable(projectName);
				if (!monitor.isEnabled()) {
					toRemove.add(targetId);
				}
			}
		}
		for (String targetId : toRemove) {
			ZendServerMonitor monitor = monitors.get(targetId);
			monitor.cancel();
			monitors.remove(targetId);
		}
	}

	/**
	 * Remove monitor for specified target. If there is no monitor for that
	 * target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
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
	 * 
	 * @throws BackingStoreException
	 */
	public static void removeAll() throws BackingStoreException {
		Set<String> keys = monitors.keySet();
		for (String key : keys) {
			ZendServerMonitor monitor = monitors.get(key);
			monitor.cancel();
			monitor.flushPreferences();
		}
	}

	/**
	 * Check if specified project is deployed on least one target in a current
	 * workspace.
	 * 
	 * @param projectName
	 * @return <code>true</code> if specified project is deployed on least one
	 *         target in a current workspace; otherwise return
	 *         <code>false</code>
	 */
	public static boolean isDeployed(String projectName) {
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			ILaunchConfiguration[] launches = Startup.getLaunches(target);
			if (launches != null && launches.length > 0) {
				for (ILaunchConfiguration config : launches) {
					try {
						String name = config.getAttribute(Startup.PROJECT_NAME,
								(String) null);
						if (projectName.equals(name)) {
							return true;
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * Set preference value for specified project for each target where it is
	 * deployed and launch configuration is available.
	 * 
	 * @param projectName
	 * @param enable
	 */
	public static void setEnabled(String projectName, boolean enable) {
		List<ILaunchConfiguration> launches = getLaunches(projectName);
		for (ILaunchConfiguration config : launches) {
			try {
				String targetId = config.getAttribute(Startup.TARGET_ID,
						(String) null);
				if (targetId != null) {
					ZendServerMonitor monitor = monitors.get(targetId);
					if (monitor != null) {
						monitor.setEnabled(projectName, enable);
					} else {
						ZendServerMonitor m = new ZendServerMonitor(targetId,
								projectName, null);
						m.setEnabled(projectName, enable);
					}
				}
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
	}

	/**
	 * 
	 * Set preference value for specified project and target.
	 * 
	 * @param projectName
	 * @param enable
	 */
	public static void setEnabled(String targetId, String projectName,
			boolean enable) {
		ZendServerMonitor monitor = monitors.get(targetId);
		if (monitor != null) {
			monitor.setEnabled(projectName, enable);
		} else {
			ZendServerMonitor m = new ZendServerMonitor(targetId, projectName,
					null);
			m.setEnabled(projectName, enable);
		}
	}

	private static List<ILaunchConfiguration> getLaunches(String projectName) {
		List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			ILaunchConfiguration[] launches = Startup.getLaunches(target);
			if (launches != null && launches.length > 0) {
				for (ILaunchConfiguration config : launches) {
					try {
						String name = config.getAttribute(Startup.PROJECT_NAME,
								(String) null);
						if (projectName.equals(name)) {
							result.add(config);
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return result;
	}

}
