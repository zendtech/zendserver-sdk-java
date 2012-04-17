/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;

/**
 * Command handler responsible for enabling event monitoring for selected
 * application.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EnableApplicationMonitorHandler extends AbstractMonitoringHandler {

	protected void enableMonitoring(Object element) {
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration cfg = (ILaunchConfiguration) element;
			try {
				String targetId = cfg
						.getAttribute(DeploymentAttributes.TARGET_ID.getName(),
								(String) null);
				String baseURL = cfg.getAttribute(
						DeploymentAttributes.BASE_URL.getName(), (String) null);
				String projectName = cfg.getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(),
						(String) null);
				if (projectName != null && targetId != null && baseURL != null) {
					MonitorManager.setApplicationEnabled(targetId, projectName, true);
					MonitorManager.createApplicationMonitor(targetId, projectName, new URL(
							baseURL));
					return;
				}
			} catch (CoreException e) {
				Activator.log(e);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
	}

}
