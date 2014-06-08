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
 * Handler for a quick deploy command. It performs deploy action without showing
 * Deployment wizard. It uses default deployment parameters base on selected
 * project and its server (see
 * {@link LaunchUtils#createDefaultHelper(String, IProject)} to see more
 * details). Action can be performed only if project's server has deployment
 * enabled.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class QuickDeployProjectHandler extends AbstractDeploymentHandler {

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
			if (target != null) {
				execute(project, target.getId());
			}
		}
		return null;
	}

	private void execute(final IProject project, final String targetId) {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return;
		}
		Job job = new Job("Quick Deployment") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				DeploymentHandler handler = new DeploymentHandler();
				IDeploymentHelper helper = LaunchUtils.createDefaultHelper(
						targetId, project);
				helper.setDevelopmentMode(false);
				if (handler.performJob(helper, project) == IStatus.OK) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.setSystem(true);
		job.schedule();
	}

}
