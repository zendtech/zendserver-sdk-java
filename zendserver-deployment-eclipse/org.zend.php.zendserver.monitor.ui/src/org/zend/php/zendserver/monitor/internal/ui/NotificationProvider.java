/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import org.eclipse.swt.widgets.Shell;
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
	public void showNonification(Shell shell, IZendIssue issue,
			String basePath, String targetId, EventSource eventSource) {
		IBody eventBody = new EventBody(targetId, eventSource, issue, basePath);
		NotificationSettings settings = new NotificationSettings();
		settings.setTitle(Messages.NotificationProvider_Title)
				.setClosable(true).setType(NotificationType.INFO)
				.setBody(eventBody).setBorder(true);
		NotificationManager.registerNotification(NotificationManager
				.createNotification(shell, settings));
	}

}
