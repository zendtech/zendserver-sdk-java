/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import org.eclipse.ui.IStartup;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link IStartup} implementation which is responsible for enabling monitoring
 * for each target which has at least one launch configuration assigned.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class Startup implements IStartup {

	static {
		Log.getInstance().registerLogger(new ILogger() {

			public void warning(Object message) {
				Activator.logWaring(message.toString());
			}

			public void info(Object message) {
				Activator.logInfo(message.toString());
			}

			public ILogger getLogger(String creatorName, boolean verbose) {
				return this;
			}

			public void error(Object message) {
				if (message instanceof Exception) {
					Activator.log((Exception) message);
				}
			}

			public void debug(Object message) {
				Activator.logWaring(message.toString());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String targetId = target.getId();
			if (MonitorManager.isMonitorStarted(targetId)) {
				MonitorManager.createTargetMonitor(targetId);
			}
		}
	}

}
