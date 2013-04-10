/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Start debug mode handler.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class StartDebugModeHandler extends AbstractHandler {

	private static final String CONTAINER = "container"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
		if (TargetsManager.isOpenShift(target)
				|| TargetsManager.isPhpcloud(target)
				&& !SSHTunnelManager.getManager().isAvailable(target)) {
			OpenTunnelCommand openTunnelCommand = new OpenTunnelCommand();
			openTunnelCommand.execute(event);
			if (!SSHTunnelManager.getManager().isAvailable(target)) {
				return null;
			}
		}
		startDebugMode(target);
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

}
