/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Represents job which monitors particular target and notifies user about all
 * server events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class TargetMonitor extends AbstractMonitor {

	public TargetMonitor(String targetId) {
		super(targetId, Messages.TargetMonitor_JobName);
	}

	/**
	 * Set preference value for this target.
	 * 
	 * @param tar
	 * @param enable
	 */
	public void setEnabled(boolean enable) {
		getPreferences().putBoolean(targetId, enable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#disable()
	 */
	public void disable() {
		getPreferences().putBoolean(targetId, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#isEnabled()
	 */
	public boolean isEnabled() {
		return getPreferences().getBoolean(targetId, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.internal.core.AbstractMonitor#
	 * flushPreferences()
	 */
	public void flushPreferences() throws BackingStoreException {
		getPreferences().flush();
	}

	protected void handleIssues(List<IZendIssue> issues) {
		for (int i = issues.size() - 1; i >= 0; i--) {
			IZendIssue zendIssue = issues.get(i);
			Issue issue = zendIssue.getIssue();
			boolean actionsAvailable = checkActions(zendIssue);
			Date date = getTime(issue.getLastOccurance());
			if (date != null && date.getTime() >= lastTime) {
				String baseURL = issue.getGeneralDetails().getUrl();
				IProject project = getProject(baseURL);
				String basePath = null;
				if (project != null) {
					int index = baseURL.indexOf(project.getName());
					if (index != -1) {
						basePath = baseURL.substring(index
								+ project.getName().length(), baseURL.length());
					}
				}
				if (shouldNotify(issue.getSeverity())) {
					// handle case when have not found a corresponding project
					if (project != null) {
						int delay = 0;
						IPreferenceStore store = Activator.getDefault()
								.getPreferenceStore();
						if (store.getBoolean(MonitorManager.HIDE_KEY)) {
							delay = store.getInt(MonitorManager.HIDE_TIME_KEY) * 1000;
						}
						Activator.getDefault().getPreferenceStore()
								.getBoolean(targetId);
						showNonification(zendIssue, project.getName(),
								basePath, delay, actionsAvailable);
					}
				}
			}
		}
	}

	protected IProject getProject(String urlString) {
		IPath path = new Path(urlString);
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		String host = target.getHost().getHost();
		String[] segments = path.segments();
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].startsWith(host)) {
				path = path.removeFirstSegments(i + 1);
				break;
			}
		}
		if (path.segmentCount() > 0) {
			String projectName = path.segment(0);
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (project != null) {
				return project;
			}
		}
		return null;
	}

	protected boolean shouldStart() {
		return isEnabled();
	}

	private IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
	}

	private boolean shouldNotify(IssueSeverity severity) {
		IEclipsePreferences prefs = getPreferences();
		String nodeName = severity.getName().toLowerCase();
		return prefs.getBoolean(nodeName, true);
	}

}
