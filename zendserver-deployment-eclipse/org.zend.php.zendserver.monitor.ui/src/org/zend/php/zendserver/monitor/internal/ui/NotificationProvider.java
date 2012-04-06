/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.php.zendserver.monitor.core.INotificationProvider;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.monitor.IZendIssue;

/**
 * Implementation of {@link INotificationProvider}. Provides ability to display
 * server event notification.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class NotificationProvider implements INotificationProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.core.INotificationProvider#showNonification
	 * (org.zend.sdklib.monitor.IZendIssue, java.lang.String,
	 * org.zend.php.zendserver.monitor.core.EventSource)
	 */
	public void showNonification(IZendIssue issue, String targetId,
			IEventDetails eventSource) {
		IBody eventBody = new EventBody(targetId, eventSource, issue);
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(issue.getIssue().getRule()).setClosable(true)
				.setType(NotificationType.INFO).setBody(eventBody)
				.setBorder(true);
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(eventSource.getProjectName());
		if (project != null) {
			IEclipsePreferences prefs = new ProjectScope(project)
					.getNode(org.zend.php.zendserver.monitor.core.Activator.PLUGIN_ID);
			if (prefs.getBoolean(MonitorManager.HIDE_KEY, false)) {
				settings.setDelay(prefs
						.getInt(MonitorManager.HIDE_TIME_KEY, 10) * 1000);
			}
		}
		NotificationManager.registerNotification(NotificationManager
				.createNotification(settings));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.core.INotificationProvider#showProgress
	 * (java.lang.String, int,
	 * org.eclipse.jface.operation.IRunnableWithProgress)
	 */
	public void showProgress(String title, int height,
			IRunnableWithProgress runnable) {
		NotificationManager.registerProgress(title, height, runnable, false);
	}

}
