/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.commands;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;

/**
 * Property tester responsible for evaluating current monitoring enablement for
 * selected application.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ApplicationMonitorStateTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof List<?>) {
			List<?> list = (List<?>) receiver;
			for (Object object : list) {
				if (object instanceof ILaunchConfiguration) {
					ILaunchConfiguration cfg = (ILaunchConfiguration) object;
					try {
						String targetId = cfg.getAttribute(
								DeploymentAttributes.TARGET_ID.getName(),
								(String) null);
						String projectName = cfg.getAttribute(
								DeploymentAttributes.PROJECT_NAME.getName(),
								(String) null);
						if (projectName != null && targetId != null) {
							if (MonitorManager.isTargetEnabled(targetId)) {
								return false;
							}
							IEclipsePreferences scope = getPreferences(projectName);
							return scope.getBoolean("enabled." + targetId, //$NON-NLS-1$
									false) == (Boolean) expectedValue;
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return false;
	}

	private IEclipsePreferences getPreferences(String projectName) {
		IResource project = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(projectName);
		if (project instanceof IContainer) {
			return new ProjectScope(project.getProject())
					.getNode(org.zend.php.zendserver.monitor.core.Activator.PLUGIN_ID);
		}
		return null;
	}

}
