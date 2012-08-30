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
import org.zend.php.zendserver.monitor.internal.core.AbstractMonitor;
import org.zend.php.zendserver.monitor.internal.core.Startup;
import org.zend.php.zendserver.monitor.internal.core.TargetMonitor;
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
	public static final String ENABLED_ALL = "enabledAll"; //$NON-NLS-1$
	public static final String HIDE_KEY = "hide"; //$NON-NLS-1$
	public static final String HIDE_TIME_KEY = "hide_time"; //$NON-NLS-1$

	public static final int CODE_TRACE = 0x01;
	public static final int REPEAT = 0x10;

	private static Map<String, ZendServerMonitor> applicationMonitors;
	private static Map<String, TargetMonitor> targetMonitors;

	static {
		applicationMonitors = Collections
				.synchronizedMap(new HashMap<String, ZendServerMonitor>());
		targetMonitors = Collections
				.synchronizedMap(new HashMap<String, TargetMonitor>());
	}

	/**
	 * Enable application monitor for specified target and URL. If monitor
	 * already exists for that target, provided URL is added to be observed.
	 * 
	 * @param targetId
	 *            target id
	 * @param project
	 *            project name
	 * @param url
	 *            URL which will be observed
	 */
	public static boolean createApplicationMonitor(String targetId,
			String project, URL url) {
		try {
			ZendServerMonitor monitor = applicationMonitors.get(targetId);
			if (monitor == null) {
				ZendServerMonitor m = new ZendServerMonitor(targetId, project,
						url);
				if (m.start()) {
					applicationMonitors.put(targetId, m);
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
	 * Create and start application monitor for specified project. Monitor will
	 * be created only on those targets where application is deployed and launch
	 * configuration is available.
	 * 
	 * @param projectName
	 *            project name
	 */
	public static void createApplicationMonitor(String projectName) {
		List<ILaunchConfiguration> launches = getLaunches(projectName);
		for (ILaunchConfiguration config : launches) {
			try {
				String targetId = config.getAttribute(Startup.TARGET_ID,
						(String) null);
				String baseURL = config.getAttribute(Startup.BASE_URL,
						(String) null);
				if (targetId != null && baseURL != null) {
					if (!MonitorManager.isTargetEnabled(targetId)) {
						MonitorManager.createApplicationMonitor(targetId,
								projectName, new URL(baseURL));
					}
				}
			} catch (CoreException e) {
				Activator.log(e);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
	}

	/**
	 * Stop application monitor for specified target id. If there is no monitor
	 * for that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available and was cancelled;
	 *         otherwise return <code>false</code>
	 */
	public static boolean stopApplicationMonitor(String targetId) {
		ZendServerMonitor monitor = applicationMonitors.get(targetId);
		if (monitor != null) {
			monitor.cancel();
			return true;
		}
		return false;
	}

	/**
	 * Start application monitor for specified target id. If there is no monitor
	 * for that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available and was started;
	 *         otherwise return <code>false</code>
	 */
	public static boolean startApplicationMonitor(String targetId) {
		ZendServerMonitor monitor = applicationMonitors.get(targetId);
		if (monitor != null && monitor.getState() != Job.RUNNING
				&& monitor.getState() != Job.WAITING) {
			monitor.start();
			return true;
		}
		return false;
	}

	/**
	 * Remove application monitoring of URL for specified target. If there is no
	 * monitor for that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @param projectName
	 *            project name
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean removeApplicationMonitor(String targetId,
			String projectName) {
		ZendServerMonitor monitor = applicationMonitors.get(targetId);
		if (monitor != null) {
			monitor.disable(projectName);
			if (!monitor.isEnabled()) {
				monitor.disable(true);
				monitor.cancel();
				try {
					monitor.flushPreferences();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
				applicationMonitors.remove(targetId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove application monitoring of specified project from all available
	 * application monitors.
	 * 
	 * @param projectName
	 *            project name
	 */
	public static void removeApplicationMonitorsByProject(String projectName) {
		Set<String> targetsSet = applicationMonitors.keySet();
		List<String> toRemove = new ArrayList<String>();
		for (String targetId : targetsSet) {
			ZendServerMonitor monitor = applicationMonitors.get(targetId);
			if (monitor != null) {
				monitor.disable(projectName);
				if (!monitor.isEnabled()) {
					toRemove.add(targetId);
				}
			}
		}
		for (String targetId : toRemove) {
			ZendServerMonitor monitor = applicationMonitors.get(targetId);
			monitor.cancel();
			applicationMonitors.remove(targetId);
		}
	}

	/**
	 * Remove application monitor for specified target. If there is no
	 * application monitor for that target, nothing happens.
	 * 
	 * @param targetId
	 *            target id
	 * @return <code>true</code> if monitor was available; otherwise return
	 *         <code>false</code>
	 */
	public static boolean removeApplicationMonitor(String targetId) {
		ZendServerMonitor monitor = applicationMonitors.get(targetId);
		if (monitor != null) {
			monitor.cancel();
			applicationMonitors.remove(targetId);
			return true;
		}
		return false;
	}

	/**
	 * Remove all available application monitors for all targets.
	 * 
	 * @throws BackingStoreException
	 */
	public static void removeAllApplicationMonitors()
			throws BackingStoreException {
		Set<String> keys = applicationMonitors.keySet();
		for (String key : keys) {
			ZendServerMonitor monitor = applicationMonitors.get(key);
			monitor.cancel();
			monitor.flushPreferences();
		}
	}

	public static void removeAllTargetMonitors() throws BackingStoreException {
		Set<String> keys = targetMonitors.keySet();
		for (String key : keys) {
			TargetMonitor monitor = targetMonitors.get(key);
			monitor.cancel();
			monitor.flushPreferences();
		}
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
		keys = applicationMonitors.keySet();
		for (String key : keys) {
			ZendServerMonitor m = applicationMonitors.get(key);
			if (m == monitor) {
				removeApplicationMonitor(key);
				return;
			}
		}
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
				ZendServerMonitor appMonitor = applicationMonitors
						.get(targetId);
				if (appMonitor != null) {
					appMonitor.disable(false);
					removeApplicationMonitor(targetId);
				}
			} else {
				return false;
			}
		}
		return true;
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
	 * Check if specified project is deployed on least one target in a current
	 * workspace.
	 * 
	 * @param projectName
	 * @return <code>true</code> if specified project is deployed on least one
	 *         target in a current workspace; otherwise return
	 *         <code>false</code>
	 */
	public static List<IZendTarget> getDeployedTargets(String projectName) {
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		List<IZendTarget> result = new ArrayList<IZendTarget>();
		for (IZendTarget target : targets) {
			ILaunchConfiguration[] launches = Startup.getLaunches(target);
			if (launches != null && launches.length > 0) {
				for (ILaunchConfiguration config : launches) {
					try {
						String name = config.getAttribute(Startup.PROJECT_NAME,
								(String) null);
						if (projectName.equals(name)) {
							result.add(target);
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * Set preference value for specified project for each target where it is
	 * deployed and launch configuration is available.
	 * 
	 * @param projectName
	 * @param enable
	 */
	public static void setApplicationEnabled(String projectName, boolean enable) {
		List<ILaunchConfiguration> launches = getLaunches(projectName);
		for (ILaunchConfiguration config : launches) {
			try {
				String targetId = config.getAttribute(Startup.TARGET_ID,
						(String) null);
				if (targetId != null) {
					ZendServerMonitor monitor = applicationMonitors
							.get(targetId);
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
	public static void setApplicationEnabled(String targetId,
			String projectName, boolean enable) {
		ZendServerMonitor monitor = applicationMonitors.get(targetId);
		if (monitor != null) {
			monitor.setEnabled(projectName, enable);
		} else {
			ZendServerMonitor m = new ZendServerMonitor(targetId, projectName,
					null);
			m.setEnabled(projectName, enable);
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

	public static boolean isTargetEnabled(String targetId) {
		return Activator.getDefault().getPreferenceStore().getBoolean(targetId);
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
