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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * Stop debug mode handler.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class StopDebugModeHandler extends AbstractHandler {

	private IZendTarget target;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
		if (target == null) {
			throw new ExecutionException(Messages.OpenTunnelCommand_NoTarget);
		}
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
						doStopDebugMode();
						monitor.done();
					}
				}, false);

		return null;
	}

	private void doStopDebugMode() {
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
