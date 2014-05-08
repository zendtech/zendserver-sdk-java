/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.NotificationManager;
import org.zend.core.notifications.ui.INotification;
import org.zend.core.notifications.ui.INotificationChangeListener;
import org.zend.php.server.ui.actions.IActionContribution;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Contribution to the action which is responsible for starting and stopping
 * Debug Mode on selected server.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DebugModeAction extends AbstractTunnelHelper implements
		IActionContribution {

	private Server server;

	public void setServer(Server server) {
		this.server = server;
	}

	public String getLabel() {
		IZendTarget target = getTarget();
		if (target != null) {
			if (DebugModeManager.getManager().isInDebugMode(target)) {
				return Messages.DebugModeAction_StopLabel;
			} else {
				return Messages.DebugModeAction_StartLabel;
			}
		}
		Activator.log(new Exception(Messages.DebugModeAction_NoTargetMessage));
		return ""; //$NON-NLS-1$
	}

	public boolean isAvailable(Server server) {
		return getTarget() != null;
	}

	public ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(Activator.IMAGE_DEBUG_APPLICATION);
	}

	public void run() {
		IZendTarget target = getTarget();
		if (target != null) {
			if (!DebugModeManager.getManager().isInDebugMode(target)) {
				start(target);
			} else {
				stop(target);
			}
		}
	}

	private void stop(final IZendTarget target) {
		NotificationManager.registerProgress(
				Messages.DebugModeHandler_DebugModeLabel,
				Messages.DebugModeHandler_StoppingDebugMode,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(
								Messages.DebugModeHandler_StoppingDebugMode,
								IProgressMonitor.UNKNOWN);
						doStopDebugMode(target);
						monitor.done();
					}
				}, false);
	}

	private void start(IZendTarget target) {
		if (TargetsManager.isOpenShift(target)
				|| TargetsManager.isPhpcloud(target)
				&& !SSHTunnelManager.getManager().isAvailable(target)) {
			final IZendTarget finalTarget = target;
			openTunnel(target, new INotificationChangeListener() {

				public void statusChanged(INotification notification) {
					if (SSHTunnelManager.getManager().isAvailable(finalTarget)) {
						startDebugMode(finalTarget);
					}
				}
			});
		} else {
			startDebugMode(target);
		}
	}

	private IZendTarget getTarget() {
		if (server != null) {
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			if (server != null) {
				String serverName = server.getName();
				IZendTarget[] targets = manager.getTargets();
				for (IZendTarget target : targets) {
					if (serverName.equals(target.getServerName())) {
						return target;
					}
				}
			}
		}
		return null;
	}

	private void startDebugMode(final IZendTarget target) {
		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		NotificationManager.registerProgress(
				Messages.DebugModeHandler_DebugModeLabel,
				Messages.DebugModeHandler_StartingDebugMode,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(
								Messages.DebugModeHandler_StartingDebugMode,
								IProgressMonitor.UNKNOWN);
						doStartDebugMode(target, shell);
						monitor.done();
					}

				}, false);
	}

	private void doStartDebugMode(final IZendTarget target, final Shell shell) {
		IStatus status = DebugModeManager.getManager().startDebugMode(target);
		switch (status.getSeverity()) {
		case IStatus.OK:
			NotificationManager.registerInfo(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		case IStatus.WARNING:
			shell.getDisplay().asyncExec(new Runnable() {

				public void run() {
					boolean shouldRestart = MessageDialog
							.openQuestion(
									shell,
									Messages.DebugModeHandler_DebugModeLabel,
									Messages.StartDebugModeHandler_DebugStartedQuestionMessage);
					if (shouldRestart) {
						restartDebugMode(target);
					}
				}
			});
			break;
		case IStatus.ERROR:
			NotificationManager.registerError(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		default:
			break;
		}
	}

	private void restartDebugMode(IZendTarget target) {
		IStatus status = DebugModeManager.getManager().stopDebugMode(target);
		switch (status.getSeverity()) {
		case IStatus.OK:
			startDebugMode(target);
			break;
		case IStatus.WARNING:
			NotificationManager.registerWarning(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		case IStatus.ERROR:
			NotificationManager.registerError(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		default:
			break;
		}
	}

	private void doStopDebugMode(IZendTarget target) {
		IStatus status = DebugModeManager.getManager().stopDebugMode(target);
		switch (status.getSeverity()) {
		case IStatus.OK:
			NotificationManager.registerInfo(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		case IStatus.WARNING:
			NotificationManager.registerWarning(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		case IStatus.ERROR:
			NotificationManager.registerError(
					Messages.DebugModeHandler_DebugModeLabel,
					status.getMessage(), 4000);
			break;
		default:
			break;
		}
	}

}
