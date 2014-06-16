/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnel.State;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.core.tunnel.TunnelException;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.monitor.core.Activator;

import com.jcraft.jsch.JSchException;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public abstract class AbstractTunnelHelper {

	protected void openTunnel(final SSHTunnelConfiguration config) {
		NotificationManager.registerProgress(Messages.OpenTunnelCommand_Title,
				Messages.OpenTunnelCommand_Message,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(Messages.OpenTunnelCommand_Message,
								IProgressMonitor.UNKNOWN);
						doOpenTunnel(config);
						monitor.done();
					}

				}, false);
	}

	protected void openTunnel(final SSHTunnelConfiguration config,
			final INotificationChangeListener listener) {
		NotificationManager.registerProgress(Messages.OpenTunnelCommand_Title,
				Messages.OpenTunnelCommand_Message,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(Messages.OpenTunnelCommand_Message,
								IProgressMonitor.UNKNOWN);
						doOpenTunnel(config);
						monitor.done();
					}

				}, false, listener);
	}

	protected void closeTunnel(final String host) {
		NotificationManager.registerProgress(Messages.OpenTunnelCommand_Title,
				"Closing SSH Tunnel...", new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask("Closing SSH Tunnel...",
								IProgressMonitor.UNKNOWN);
						SSHTunnelManager.getManager().disconnect(host);
						monitor.done();
					}

				}, false);
	}

	private boolean doOpenTunnel(SSHTunnelConfiguration config) {
		try {
			State result = null;
			if (config.isEnabled()
					&& !SSHTunnelManager.getManager().isConnected(
							config.getHost())) {
				result = SSHTunnelManager.getManager().connect(config);
			}
			switch (result) {
			case CONNECTED:
				String message = MessageFormat.format(
						Messages.OpenTunnelCommand_TunnelOpenedMessage,
						config.getHost());
				NotificationManager.registerInfo(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
				break;
			case CONNECTING:
				message = MessageFormat.format(
						Messages.OpenTunnelCommand_SuccessMessage,
						config.getHost());
				NotificationManager.registerInfo(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
				break;
			case NOT_SUPPORTED:
				NotificationManager.registerWarning(
						Messages.OpenTunnelCommand_OpenTunnelTitle,
						Messages.OpenTunnelCommand_NotSupportedMessage, 4000);
				break;
			case ERROR:
				message = MessageFormat.format(
						Messages.DeploymentHandler_sshTunnelErrorTitle,
						config.getHost());
				NotificationManager.registerError(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
			default:
				break;
			}
			return true;
		} catch (TunnelException e) {
			Activator.log(e);
			String message = e.getMessage();
			NotificationManager.registerError(
					Messages.OpenTunnelCommand_OpenTunnelTitle, message, 5000);
		} catch (JSchException e) {
			Activator.log(e);
			String message = MessageFormat.format(
					Messages.DeploymentHandler_sshTunnelErrorTitle,
					config.getHost(), e.getMessage());
			NotificationManager.registerError(
					Messages.OpenTunnelCommand_OpenTunnelTitle, message, 5000);
		}
		return false;
	}

}
