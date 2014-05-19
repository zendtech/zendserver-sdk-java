/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core.config;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.core.Activator;
import org.zend.php.zendserver.deployment.debug.core.IDeploymentContribution;
import org.zend.sdklib.target.IZendTarget;

/**
 * Deployment contribution responsible for updating server setting of deployed
 * project.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class UpdateServerContribution implements IDeploymentContribution {

	public IStatus performAfter(IProgressMonitor monitor,
			IDeploymentHelper helper) {
		String projectName = helper.getProjectName();
		if (projectName != null && !projectName.isEmpty()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (project != null) {
				IZendTarget target = TargetsManagerService.INSTANCE
						.getTargetManager().getTargetById(helper.getTargetId());
				if (target != null) {
					Server server = DeploymentUtils.findExistingServer(target);
					ServersManager.setDefaultServer(project, server);
				}
				String file = helper.getBaseURL().getFile();
				if (file.isEmpty()) {
					file = "/"; //$NON-NLS-1$
				}
				PHPProjectPreferences.setDefaultBasePath(project, file);

				ProjectScope projectScope = new ProjectScope(project);
				IEclipsePreferences node = projectScope
						.getNode(IPHPDebugConstants.DEBUG_QUALIFIER);
				node.putBoolean(IPHPDebugConstants.DEBUG_PER_PROJECT, true);
				try {
					node.flush();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
			}
		}
		return Status.OK_STATUS;
	}

	public IStatus performBefore(IProgressMonitor monitor,
			IDeploymentHelper helper) {
		return Status.OK_STATUS;
	}

}
