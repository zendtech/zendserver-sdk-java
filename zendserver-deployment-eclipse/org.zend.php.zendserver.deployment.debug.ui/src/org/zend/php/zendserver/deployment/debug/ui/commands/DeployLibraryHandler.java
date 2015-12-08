/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.contributions.TestingSectionContribution;
import org.zend.php.zendserver.deployment.debug.ui.wizards.LibraryDeploymentUtils;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployLibraryHandler extends AbstractDeploymentHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.
	 * commands .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String mode = event.getParameter(TestingSectionContribution.MODE);

		Object obj = event.getApplicationContext();
		IEvaluationContext ctx = null;
		if (obj instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) obj;
		}

		IProject[] projects = null;
		String targetId = null;

		if (ctx != null) {
			projects = getProjects(ctx.getVariable(TestingSectionContribution.PROJECT_NAME));
			Object targetIdVariable = ctx.getVariable(TestingSectionContribution.TARGET_ID);
			if (targetIdVariable instanceof String) {
				targetId = (String) targetIdVariable;
			}
		}
		if (projects == null) {
			projects = getProjects(event.getParameter(TestingSectionContribution.PROJECT_NAME));
		}
		if (projects == null) {
			projects = new IProject[] { getProjectFromEditor() };
		}

		for (IProject project : projects) {
			execute(mode, project, targetId);
		}

		return null;
	}

	private void execute(final String mode, final IProject project, final String targetId) {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return;
		}
		try {
			if (!hasDeploymentNature(project)) {
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				if (MessageDialog.openConfirm(shell, Messages.DeployLibraryHandler_WarningTitle,
						MessageFormat.format(Messages.DeployLibraryHandler_WarningMessage, project.getName()))) {
					enableDeployment(project);
				} else {
					return;
				}
			}
		} catch (CoreException e) {
			Activator.log(e);
		}

		LibraryDeploymentUtils handler = new LibraryDeploymentUtils();
		handler.openLibraryDeploymentWizard(project, targetId);
	}

}
