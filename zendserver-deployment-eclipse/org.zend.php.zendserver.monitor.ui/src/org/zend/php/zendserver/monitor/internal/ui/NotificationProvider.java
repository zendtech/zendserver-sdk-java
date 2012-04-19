/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.NotificationType;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.php.zendserver.monitor.core.INotificationProvider;
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
	 * org.zend.php.zendserver.monitor.core.IEventDetails, int)
	 */
	public void showNonification(IZendIssue issue, String targetId,
			IEventDetails eventSource, int delay, boolean actionsAvailable) {
		NotificationSettings settings = getNotificationSettings(issue,
				targetId, eventSource, actionsAvailable);
		if (delay != 0) {
			settings.setDelay(delay);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.core.INotificationProvider#showErrorMessage
	 * (java.lang.String, java.lang.String)
	 */
	public void showErrorMessage(String title, String message) {
		NotificationManager.registerError(title, message, 6000);
	}

	private NotificationSettings getNotificationSettings(IZendIssue issue,
			String targetId, IEventDetails eventSource, boolean actionsAvailable) {
		IBody eventBody = new EventBody(targetId, eventSource, issue,
				actionsAvailable);
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(issue.getIssue().getRule()).setClosable(true)
				.setType(NotificationType.INFO).setBody(eventBody)
				.setBorder(true);
		if (eventSource != null && eventSource.getSourceFile() != null) {
			settings.setComparator(getComparator(issue, eventSource));
		}
		return settings;
	}

	private EventComparator getComparator(IZendIssue issue,
			IEventDetails eventSource) {
		return new EventComparator(issue.getIssue().getRule(),
				eventSource.getSourceFile() + ':' + eventSource.getLine());
	}

}
