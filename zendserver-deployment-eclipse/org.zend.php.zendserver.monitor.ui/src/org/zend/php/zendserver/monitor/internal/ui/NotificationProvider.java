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
import org.zend.php.zendserver.monitor.core.EventSource;
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

	@Override
	public void showNonification(IZendIssue issue,
			String targetId, EventSource eventSource) {
		IBody eventBody = new EventBody(targetId, eventSource, issue);
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(issue.getIssue().getRule()).setClosable(true)
				.setType(NotificationType.INFO).setBody(eventBody)
				.setBorder(true);
		NotificationManager.registerNotification(NotificationManager
				.createNotification(settings));
	}

	@Override
	public void showProgress(String title, int height,
			IRunnableWithProgress runnable) {
		NotificationManager.registerProgress(title, height, runnable);
	}

}
