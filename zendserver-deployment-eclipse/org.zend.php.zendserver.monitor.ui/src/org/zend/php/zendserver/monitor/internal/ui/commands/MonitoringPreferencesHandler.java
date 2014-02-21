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
package org.zend.php.zendserver.monitor.internal.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.ui.preferences.MonitoringPreferencePage;
import org.zend.sdklib.target.IZendTarget;

public class MonitoringPreferencesHandler extends AbstractHandler {

	private static final String CONTAINER = "container"; //$NON-NLS-1$

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
		if (target != null) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
					shell, MonitoringPreferencePage.ID,
					new String[] { MonitoringPreferencePage.ID }, target);
			dialog.open();
		}
		return null;
	}

}
