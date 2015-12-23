/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationAction;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.php.server.core.types.ServerTypesManager;
import org.eclipse.ui.IStartup;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.ui.notifications.AddingLocalZendServerNotification;
import org.zend.php.zendserver.deployment.ui.notifications.LocalZendServerDetectedNotification;
import org.zend.php.zendserver.deployment.ui.notifications.base.NotificationHelper;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link IStartup} implementation responsible for detection of a local Zend
 * Server instance. Detected server will be added only if its host is not
 * already taken by another existing server.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalZendServerStartup implements IStartup {

	@Override
	public void earlyStartup() {
		fetchServerData();
	}

	private void fetchServerData() {
		Job performer = new Job(Messages.LocalZendServerStartup_RegisteringZendServer) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.LocalZendServerStartup_RegisteringZendServer, IProgressMonitor.UNKNOWN);
				NotificationAction notificationAction = NotificationHelper.getNotificationAction(LocalZendServerDetectedNotification.ID);
				if(notificationAction != null && !notificationAction.isSelected())
					return Status.OK_STATUS;
				
				//look for existing local server; it does not necessarily have to be the default one
				monitor.subTask(Messages.LocalZendServerStartup_CheckingExistingServers);
				ServerTypesManager typesManager = ServerTypesManager.getInstance();
				Server[] servers = ServersManager.getServers();
				for (Server server : servers) {
					IServerType serverType = typesManager.getType(server);
					if(LocalZendServerType.ID.equalsIgnoreCase(serverType.getId())) {
						Activator.logInfo(
								MessageFormat.format(Messages.LocalZendServerStartup_LocalZendServerExists_Info,
										server.getName()));
						return Status.OK_STATUS;
					}
				}
				
				monitor.subTask(Messages.LocalZendServerStartup_FetchingConfiguration);
				final Server server;
				try {
					server = ZendServerManager.getInstance().getLocalZendServer();
				} catch (DetectionException e) {
					String message = Messages.LocalZendServerStartup_NotFoundMessage;
					Activator.logError(message, e);
					AddingLocalZendServerNotification notification = new AddingLocalZendServerNotification(AddingLocalZendServerNotification.NotificationTypes.WARNING);
					notification.setDescription(message);
					NotificationHelper.notify(notification);
					return Status.CANCEL_STATUS;
				}
				server.setAttribute(IServerType.TYPE, LocalZendServerType.ID);

				IZendTarget zendTarget = TargetsManagerService.INSTANCE.getTargetManager().getExistingLocalhost();
				LocalZendServerDetectedNotification notification = new LocalZendServerDetectedNotification(zendTarget != null);
				NotificationHelper.notify(notification);
				return Status.OK_STATUS;
			}
		};
		performer.setUser(false);
		performer.setSystem(false);
		performer.schedule();
	}

}
