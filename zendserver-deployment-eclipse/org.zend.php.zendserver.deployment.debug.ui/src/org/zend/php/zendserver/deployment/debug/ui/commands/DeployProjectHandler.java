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
package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;
import org.zend.sdklib.target.IZendTarget;

/**
 * Handler for a deploy command. It opens Deployment wizard for selected
 * project.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class DeployProjectHandler extends AbstractDeploymentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty())
			return null;

		Object element = null;
		if (selection instanceof IStructuredSelection) {
			element = ((IStructuredSelection) selection).getFirstElement();
		}

		if (element instanceof IAdaptable) {
			IResource res = (IResource) ((IAdaptable) element)
					.getAdapter(IResource.class);
			IProject project = res.getProject();
			IZendTarget target = ServerUtils.getTarget(project);
			String targetId = null;
			if (target != null) {
				targetId = target.getId();
			}
			execute(project, targetId);
		}
		return null;
	}

	private void execute(final IProject project, final String targetId) {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return;
		}
		Job job = new Job("Deployment") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				DeploymentHandler handler = new DeploymentHandler();
				IDeploymentHelper defaultHelper = null;
				if (targetId != null) {
					defaultHelper = LaunchUtils.createDefaultHelper(targetId, project);
				} else {
					defaultHelper = LaunchUtils.createDefaultHelper(project);
				}
				if (handler.openNoConfigDeploymentWizard(defaultHelper, project) != IStatus.OK) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
}
