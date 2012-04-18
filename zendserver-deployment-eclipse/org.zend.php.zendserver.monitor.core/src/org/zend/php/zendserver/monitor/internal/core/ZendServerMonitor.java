/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Represents job which monitors particular application on specified target and
 * notifies user about server events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ZendServerMonitor extends AbstractMonitor {

	private Map<IProject, URL> applications;
	private Map<IProject, IEclipsePreferences> preferences;

	public ZendServerMonitor(String targetId, String project, URL url) {
		super(targetId, Messages.ZendServerMonitor_JobTitle);
		this.applications = new HashMap<IProject, URL>();
		this.preferences = new HashMap<IProject, IEclipsePreferences>();
		if (!applications.containsKey(project)) {
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(project);
			if (res instanceof IContainer) {
				applications.put(res.getProject(), url);
				setDefaultSeverities(res.getProject());
			}
		}
	}

	/**
	 * Enable monitoring for specified application's base URL.
	 * 
	 * @param project
	 * 
	 * @param url
	 *            base URL
	 */
	public void enable(String project, URL url) {
		if (!applications.containsKey(project) && shouldStart(project)) {
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(project);
			if (res instanceof IContainer) {
				applications.put(res.getProject(), url);
				setDefaultSeverities(res.getProject());
			}
		}
	}

	/**
	 * Set preference value for specified project.
	 * 
	 * @param projectName
	 * @param enable
	 */
	public void setEnabled(String projectName, boolean enable) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(projectName);
		if (res instanceof IContainer) {
			IEclipsePreferences prefs = getPreferences(res.getProject());
			if (prefs != null) {
				String enabledKey = MonitorManager.ENABLED + targetId;
				if (prefs.get(enabledKey, null) != null) {
					prefs.putBoolean(enabledKey, enable);
				}
			}
		}
	}

	/**
	 * Disable monitoring for specified application's base URL.
	 * 
	 * @param projectName
	 *            project name
	 */
	public void disable(String projectName) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(projectName);
		if (res instanceof IContainer) {
			IEclipsePreferences prefs = getPreferences(res.getProject());
			if (prefs != null
					&& prefs.getBoolean(MonitorManager.ENABLED + targetId,
							false)) {
				prefs.putBoolean(MonitorManager.ENABLED + targetId, false);
			}
			applications.remove(res.getProject());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#disable()
	 */
	public void disable() {
		Set<IProject> keys = preferences.keySet();
		for (IProject project : keys) {
			IEclipsePreferences prefs = getPreferences(project);
			if (prefs != null
					&& prefs.getBoolean(MonitorManager.ENABLED + targetId,
							false)) {
				prefs.putBoolean(MonitorManager.ENABLED + targetId, false);
			}
			applications.remove(project);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#isEnabled()
	 */
	public boolean isEnabled() {
		return applications.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#
	 * flushPreferences()
	 */
	public void flushPreferences() throws BackingStoreException {
		Set<IProject> keys = preferences.keySet();
		for (IProject project : keys) {
			IEclipsePreferences prefs = preferences.get(project);
			prefs.flush();
		}
	}

	protected void handleIssues(List<IZendIssue> issues) {
		for (int i = issues.size() - 1; i >= 0; i--) {
			IZendIssue zendIssue = issues.get(i);
			Issue issue = zendIssue.getIssue();
			Date date = getTime(issue.getLastOccurance());
			if (date != null && date.getTime() >= lastTime) {
				String basePath = issue.getGeneralDetails().getUrl();
				IProject project = getProject(basePath);
				if (project != null
						&& shouldNotify(project, issue.getSeverity())) {
					IEclipsePreferences prefs = new ProjectScope(project)
							.getNode(org.zend.php.zendserver.monitor.core.Activator.PLUGIN_ID);
					int delay = 0;
					if (prefs.getBoolean(MonitorManager.HIDE_KEY, false)) {
						delay = prefs.getInt(MonitorManager.HIDE_TIME_KEY, 10) * 1000;
					}
					showNonification(zendIssue, project.getName(),
							createBasePath(applications.get(project)), delay);
				}
			}
		}
	}

	protected IProject getProject(String urlString) {
		try {
			URL url = new URL(urlString);
			String host = url.getHost();
			String base = url.getPath();
			if (host != null && base != null) {
				Set<IProject> keys = applications.keySet();
				for (IProject project : keys) {
					URL appUrl = applications.get(project);
					if (base.startsWith(appUrl.getPath())
							&& host.equals(appUrl.getHost())) {
						return project;
					}
				}
			}
		} catch (MalformedURLException e) {
			Activator.log(e);
		}
		return null;
	}

	protected boolean shouldStart() {
		Set<IProject> keys = applications.keySet();
		for (IProject project : keys) {
			if (shouldStart(project.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean shouldNotify(IProject project, IssueSeverity severity) {
		IEclipsePreferences prefs = getPreferences(project);
		String nodeName = severity.getName().toLowerCase();
		String val = prefs.get(nodeName, (String) null);
		if (val != null && Boolean.valueOf(val)) {
			return true;
		}
		return false;
	}

	private String createBasePath(URL url) {
		if (url != null && url.getPath() != null) {
			String path = url.getPath();
			if (path != null) {
				return path.substring(1);
			}
		}
		return null;
	}

	private IEclipsePreferences getPreferences(IProject project) {
		IEclipsePreferences prefs = preferences.get(project);
		if (prefs == null) {
			preferences.put(project,
					new ProjectScope(project).getNode(Activator.PLUGIN_ID));
			return preferences.get(project);
		}
		return prefs;
	}

	private boolean shouldStart(String projectName) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(projectName);
		if (res instanceof IContainer) {
			IEclipsePreferences prefs = getPreferences(res.getProject());
			if (prefs != null) {
				String enabledKey = MonitorManager.ENABLED + targetId;
				if (prefs.get(enabledKey, null) == null) {
					prefs.putBoolean(enabledKey, true);
					return true;
				} else {
					return prefs.getBoolean(enabledKey, false);
				}
			}
		}
		return false;
	}

	private void setDefaultSeverities(IProject project) {
		IssueSeverity[] severityValues = IssueSeverity.values();
		IEclipsePreferences prefs = getPreferences(project);
		for (IssueSeverity severity : severityValues) {
			String nodeName = severity.getName().toLowerCase();
			String val = prefs.get(nodeName, (String) null);
			if (val == null) {
				if (IssueSeverity.CRITICAL == severity
						|| IssueSeverity.WARNING == severity) {
					prefs.putBoolean(nodeName, true);
				}
			}
		}
	}

}
