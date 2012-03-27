/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IStartup;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link IStartup} implementation which is responsible for enabling monitoring
 * for each target which has at least one launch configuration assigned.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class Startup implements IStartup {

	private static final String LAUNCH_CONFIG_TYPE = "org.eclipse.php.debug.core.launching.webPageLaunch"; //$NON-NLS-1$
	private static final String TARGET_ID = "org.zend.php.zendserver.deployment.targetId"; //$NON-NLS-1$
	private static final String BASE_URL = "org.zend.php.zendserver.deployment.baseURL"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "org.zend.php.zendserver.deployment.projectName"; //$NON-NLS-1$

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
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			ILaunchConfiguration[] launches = getLaunches(target);
			if (launches != null && launches.length > 0) {
				for (ILaunchConfiguration config : launches) {
					try {
						String targetId = config.getAttribute(TARGET_ID,
								(String) null);
						String baseURL = config.getAttribute(BASE_URL,
								(String) null);
						String projectName = config.getAttribute(PROJECT_NAME,
								(String) null);
						if (targetId != null && baseURL != null
								&& projectName != null) {
							MonitorManager.create(targetId, projectName,
									new URL(baseURL));
						}
					} catch (MalformedURLException e) {
						Activator.log(e);
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
	}

	public ILaunchConfiguration[] getLaunches(IZendTarget target) {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = mgr
				.getLaunchConfigurationType(LAUNCH_CONFIG_TYPE);

		String id = target.getId();

		ILaunchConfiguration[] launchConfigs;
		try {
			launchConfigs = mgr.getLaunchConfigurations(type);
		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
		for (ILaunchConfiguration config : launchConfigs) {
			try {
				String targetId = config.getAttribute(TARGET_ID, (String) null);
				if (id.equals(targetId)) {
					result.add(config);
				}
			} catch (CoreException e) {
				Activator.log(e);
			}
		}

		return result.toArray(new ILaunchConfiguration[result.size()]);
	}

}
