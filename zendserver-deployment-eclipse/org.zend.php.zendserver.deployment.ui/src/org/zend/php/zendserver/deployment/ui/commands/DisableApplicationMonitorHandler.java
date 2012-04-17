/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.commands;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;

/**
 * Command handler responsible for disabling event monitoring for selected
 * application.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DisableApplicationMonitorHandler extends AbstractMonitoringHandler {

	protected void enableMonitoring(Object element) {
		if (element instanceof ILaunchConfiguration) {
			final ILaunchConfiguration cfg = (ILaunchConfiguration) element;
			try {
				String targetId = cfg
						.getAttribute(DeploymentAttributes.TARGET_ID.getName(),
								(String) null);
				String projectName = cfg.getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
				if (targetId != null && projectName != null) {
					MonitorManager.removeApplicationMonitor(targetId, projectName);
				}
				return;
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
	}

}
