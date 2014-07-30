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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
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
@SuppressWarnings("restriction")
public class TargetMonitor extends AbstractMonitor {

	private List<String> filters;

	public TargetMonitor(String targetId) {
		super(targetId, Messages.TargetMonitor_JobName);
		this.filters = getFilters();
	}

	public void updateFilters() {
		setFilters(getFilters());
	}

	public List<String> getFilters() {
		IZendTarget target = getTarget();
		Server server = ServerUtils.getServer(target);
		String value = server.getAttribute(MonitorManager.FILTERS_ATTRIBUTE,
				null);
		if (value == null) {
			value = MonitorManager.getPreferences().get(
					MonitorManager.getFiltersKey(targetId), null);
		}
		if (value != null) {
			return new ArrayList<String>(Arrays.asList(value
					.split(MonitorManager.FILTER_SEPARATOR)));
		}
		return new ArrayList<String>();
	}

	public String getValue(List<String> list) {
		StringBuilder builder = new StringBuilder();
		if (list != null && !list.isEmpty()) {
			for (String val : list) {
				builder.append(val).append(MonitorManager.FILTER_SEPARATOR);
			}
			return builder.substring(0, builder.length() - 1);
		}
		return ""; //$NON-NLS-1$
	}

	protected void handleIssues(List<IZendIssue> issues, IZendTarget target) {
		for (int i = issues.size() - 1; i >= 0; i--) {
			IZendIssue zendIssue = issues.get(i);
			Issue issue = zendIssue.getIssue();
			int actionsAvailable = checkActions(zendIssue);
			if (!isZS6(target)
					&& monitor.getTime(issue.getLastOccurance(), target) < lastTime) {
				continue;
			}
			final String baseURL = issue.getGeneralDetails().getUrl();
			IProject project = getProject(baseURL);
			String basePath = null;
			if (project != null) {
				basePath = getBasePath(baseURL, project);
			}
			if (shouldNotify(issue.getSeverity(), baseURL)) {
				// handle case when have not found a corresponding project
				if (project != null) {
					int delay = 0;
					if (MonitorManager.getHide(targetId)) {
						delay = MonitorManager.getHideTime(targetId) * 1000;
					}
					showNonification(zendIssue, project.getName(), basePath,
							delay, actionsAvailable);
				}
			}
		}
	}

	protected IProject getProject(String urlString) {
		IProject project = null;
		if (urlString != null) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			int maxLength = 0;
			for (IProject p : projects) {
				String url = LaunchUtils.getURLFromPreferences(p.getName());
				if (url != null && urlString.startsWith(url)) {
					int length = url.length();
					if (maxLength < length) {
						project = p;
						maxLength = url.length();
					}
				}
			}
		}
		if (project == null) {
			IPath path = new Path(urlString);
			IZendTarget target = TargetsManagerService.INSTANCE
					.getTargetManager().getTargetById(targetId);
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
				project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
			}
		}
		return project;
	}

	private String getBasePath(final String baseURL, IProject project) {
		String basePath = MonitorManager.SLASH;
		String url = LaunchUtils.getURLFromPreferences(project.getName());
		if (url != null && url.length() <= baseURL.length()) {
			try {
				URL base = new URL(baseURL);
				URL urlPrefs = new URL(url);
				if (base.getHost().equals(urlPrefs.getHost())) {
					if (url.endsWith(MonitorManager.SLASH)) {
						url = url.substring(0, url.length() - 1);
					}
					basePath = baseURL.substring(url.length());
				}
			} catch (MalformedURLException e) {
				// should not appear
			}
		} else {
			String toFind = MonitorManager.SLASH + project.getName();
			int index = baseURL.indexOf(toFind);
			if (index != -1) {
				if (index + toFind.length() == baseURL.length()
						|| baseURL.endsWith(toFind + MonitorManager.SLASH)) {
					basePath = MonitorManager.SLASH;
				} else {
					basePath = baseURL.substring(index + toFind.length(),
							baseURL.length());
				}
			}
		}
		return basePath;
	}

	private boolean shouldNotify(IssueSeverity severity, String baseURL) {
		if (checkSeverity(severity.getName())) {
			for (String filter : filters) {
				if (filter.endsWith(MonitorManager.SLASH)) {
					filter = filter.substring(0, filter.length() - 1);
				}
				if (baseURL.startsWith(filter)) {
					baseURL = baseURL.substring(filter.length());
					if (!baseURL.isEmpty() && baseURL.startsWith(":")) { //$NON-NLS-1$
						int index = baseURL.indexOf(MonitorManager.SLASH);
						baseURL = baseURL.substring(index);
					}
					if (baseURL.startsWith(MonitorManager.SLASH)
							|| baseURL.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkSeverity(String name) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		Server server = ServerUtils.getServer(target);
		if (server != null) {
			String value = server.getAttribute(
					MonitorManager.SEVERITY_ATTRIBUTE + name, (String) null);
			if (value != null) {
				return Boolean.valueOf(value);
			}
		}
		return MonitorManager.getPreferences().getBoolean(
				targetId + '.' + name, true);
	}

	private void setFilters(List<String> filters) {
		this.filters = filters;
	}

}
