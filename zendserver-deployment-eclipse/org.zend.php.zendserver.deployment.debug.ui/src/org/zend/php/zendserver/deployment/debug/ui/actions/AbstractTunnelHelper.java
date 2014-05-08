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
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.AbstractSSHTunnel.State;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.core.tunnel.TunnelException;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

import com.jcraft.jsch.JSchException;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public abstract class AbstractTunnelHelper {

	private static final String CONTAINER = "container"; //$NON-NLS-1$

	protected void openTunnel(final IZendTarget target) {
		NotificationManager.registerProgress(Messages.OpenTunnelCommand_Title,
				Messages.OpenTunnelCommand_Message,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(Messages.OpenTunnelCommand_Message,
								IProgressMonitor.UNKNOWN);
						doOpenTunnel(target);
						monitor.done();
					}

				}, false);
	}

	protected void openTunnel(final IZendTarget target,
			final INotificationChangeListener listener) {
		NotificationManager.registerProgress(Messages.OpenTunnelCommand_Title,
				Messages.OpenTunnelCommand_Message,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(Messages.OpenTunnelCommand_Message,
								IProgressMonitor.UNKNOWN);
						doOpenTunnel(target);
						monitor.done();
					}

				}, false, listener);
	}

	protected IZendTarget getTarget(ExecutionEvent event)
			throws ExecutionException {
		String containerName = event.getParameter(CONTAINER);
		IZendTarget target = null;
		if (containerName != null) {
			target = TargetsManagerService.INSTANCE
					.getContainerByName(containerName);
		} else {
			IEvaluationContext ctx = (IEvaluationContext) event
					.getApplicationContext();
			Object element = ctx.getDefaultVariable();
			if (element instanceof List) {
				List<?> list = (List<?>) element;
				if (list.size() > 0) {
					element = list.get(0);
				}
			}
			if (element instanceof IZendTarget) {
				target = (IZendTarget) element;
			}
		}
		if (target == null) {
			throw new ExecutionException(NLS.bind(
					Messages.OpenTunnelCommand_UnknownContainer, containerName));
		}
		return target;
	}

	private boolean doOpenTunnel(IZendTarget target) {
		try {
			State result = null;
			if (TargetsManager.isPhpcloud(target)
					|| TargetsManager.isOpenShift(target)) {
				result = SSHTunnelManager.getManager().connect(target);
			}
			switch (result) {
			case CONNECTED:
				String message = MessageFormat.format(
						Messages.OpenTunnelCommand_TunnelOpenedMessage,
						target.getId());
				NotificationManager.registerInfo(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
				break;
			case CONNECTING:
				message = MessageFormat.format(
						Messages.OpenTunnelCommand_SuccessMessage,
						target.getId());
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
						target.getId());
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
					target.getId());
			NotificationManager.registerError(
					Messages.OpenTunnelCommand_OpenTunnelTitle, message, 5000);
		}
		return false;
	}

}
