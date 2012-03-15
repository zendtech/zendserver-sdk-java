/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.EventSource;
import org.zend.php.zendserver.monitor.core.INotificationProvider;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.sdklib.monitor.ZendMonitor;
import org.zend.sdklib.monitor.ZendMonitor.Filter;
import org.zend.webapi.core.connection.data.GeneralDetails;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Represents job which monitors particular target and notifies user about
 * server events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ZendServerMonitor extends Job {

	private static final String PROVIDER_EXTENSION = "org.zend.php.zendserver.monitor.core.notificationProvider"; //$NON-NLS-1$

	private static INotificationProvider provider;

	private String targetId;
	private Map<IProject, URL> applications;
	private ZendMonitor monitor;
	private long lastTime;
	private int jobDelay = 3000;
	private int offset;
	private ZendCodeTracing codeTracing;

	public ZendServerMonitor(String targetId, String project, URL url) {
		super(Messages.ZendServerMonitor_JobTitle);
		this.targetId = targetId;
		this.applications = new HashMap<IProject, URL>();
		enable(project, url);
	}

	/**
	 * Start monitor job.
	 */
	public void start() {
		if (codeTracing == null) {
			codeTracing = new ZendCodeTracing(targetId);
			codeTracing.enable(true);
		}
		if (shouldStart()) {
			lastTime = Long.MAX_VALUE;
			setSystem(true);
			getProvider().showProgress(Messages.ZendServerMonitor_JobTitle, 90,
					new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							ZendServerMonitor.this.run(monitor);
						}
					});
		}
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(MessageFormat.format(
				Messages.ZendServerMonitor_TaskTitle, targetId),
				IProgressMonitor.UNKNOWN);
		List<IZendIssue> issues = null;
		if (this.monitor == null) {
			this.monitor = connect(monitor);
			issues = this.monitor.getOpenIssues();
		} else {
			issues = this.monitor.getIssues(Filter.ALL_OPEN_EVENTS, offset);
		}
		if (issues != null && issues.size() > 0) {
			handleIssues(issues);
			offset += issues.size();
			Date lastDate = getTime(issues.get(issues.size() - 1).getIssue()
					.getLastOccurance());
			if (lastDate != null) {
				lastTime = lastDate.getTime();
			}
		}
		if (!monitor.isCanceled()) {
			monitor.done();
			this.schedule(jobDelay);
			return Status.OK_STATUS;
		}
		monitor.done();
		return Status.CANCEL_STATUS;
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
			if (prefs != null) {
				prefs.remove(MonitorManager.ENABLED + targetId);
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
			}
			applications.remove(res.getProject());
		}
	}

	/**
	 * @return <code>true</code> if it monitors at least one application;
	 *         otherwise return <code>false</code>
	 */
	public boolean isEnabled() {
		return applications.size() > 0;
	}

	private void handleIssues(List<IZendIssue> issues) {
		for (int i = issues.size() - 1; i >= 0; i--) {
			IZendIssue zendIssue = issues.get(i);
			Issue issue = zendIssue.getIssue();
			Date date = getTime(issue.getLastOccurance());
			if (date != null && date.getTime() >= lastTime) {
				String basePath = issue.getGeneralDetails().getUrl();
				IProject project = getProject(basePath);
				if (project != null
						&& shouldNotify(project, issue.getSeverity())) {
					showNonification(zendIssue, project.getName(),
							createBasePath(applications.get(project)));
				}
			}
		}
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

	private void showNonification(final IZendIssue issue, String projectName,
			String basePath) {
		final EventSource eventSource = getEventSource(issue, projectName,
				basePath);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				getProvider().showNonification(issue, targetId, eventSource);
			}
		});
	}

	private EventSource getEventSource(IZendIssue issue, String projectName,
			String basePath) {
		GeneralDetails generalDetails = issue.getIssue().getGeneralDetails();
		String sourceFile = generalDetails.getSourceFile();
		long line = generalDetails.getSourceLine();
		return new EventSource(projectName, basePath, line, sourceFile);
	}

	private ZendMonitor connect(IProgressMonitor monitor) {
		ZendMonitor zendMonitor = new ZendMonitor(targetId);
		return zendMonitor;
	}

	private IProject getProject(String urlString) {
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

	private String createBasePath(URL url) {
		if (url != null && url.getPath() != null) {
			String path = url.getPath();
			if (path != null) {
				return path.substring(1);
			}
		}
		return null;
	}

	private Date getTime(String time) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm"); //$NON-NLS-1$
		Date date = null;
		try {
			date = formatter.parse(time);
		} catch (ParseException e) {
			Activator.log(e);
			return null;
		}
		return date;
	}

	private IEclipsePreferences getPreferences(IProject project) {
		return new ProjectScope(project).getNode(Activator.PLUGIN_ID);
	}

	private boolean shouldStart() {
		Set<IProject> keys = applications.keySet();
		for (IProject project : keys) {
			if (shouldStart(project.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean shouldStart(String projectName) {
		try {
			IResource res = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(projectName);
			if (res instanceof IContainer) {
				IEclipsePreferences prefs = getPreferences(res.getProject());
				if (prefs != null) {
					String enabledKey = MonitorManager.ENABLED + targetId;
					if (prefs.get(enabledKey, null) == null) {
						prefs.putBoolean(enabledKey, true);
						prefs.flush();
						return true;
					} else {
						return prefs.getBoolean(enabledKey, false);
					}
				}
			}
		} catch (BackingStoreException e) {
			Activator.log(e);
		}
		return false;
	}

	private void setDefaultSeverities(IProject project) {
		IssueSeverity[] severityValues = IssueSeverity.values();
		IEclipsePreferences prefs = getPreferences(project);
		boolean isDirty = false;
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
		try {
			if (isDirty) {
				prefs.flush();
			}
		} catch (BackingStoreException e) {
			Activator.log(e);
		}
	}

	private static INotificationProvider getProvider() {
		if (provider == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(PROVIDER_EXTENSION);
			for (IConfigurationElement element : elements) {
				if ("notificationProvider".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object listener = element
								.createExecutableExtension("class"); //$NON-NLS-1$
						if (listener instanceof INotificationProvider) {
							provider = (INotificationProvider) listener;
							break;
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return provider;
	}

}
