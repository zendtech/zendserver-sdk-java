/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.debug.core.debugger.DebuggerSettingsManager;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettings;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettingsWorkingCopy;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.server.ui.IHelpContextIds;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.server.ui.types.ServerTypeUtils;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
import org.zend.sdklib.target.IZendTarget;

import com.zend.php.debug.core.debugger.ZendDebuggerHostProposalComputer;

/**
 * {@link IStartup} implementation responsible for detection of a local Zend
 * Server instance. Detected server will be added only if its host is not
 * already taken by another existing server.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalZendServerStartup implements IStartup {

	private static final String MESSAGE_ID = ServersUI.PLUGIN_ID + ".localZendServer"; //$NON-NLS-1$

	@Override
	public void earlyStartup() {
		final Server server = ZendServerManager.getInstance().getLocalZendServer(new Server());
		String location = server.getAttribute(ZendServerManager.ZENDSERVER_INSTALL_LOCATION, null);
		if (location != null && new File(location).exists()) {
			if (!isUnique(server.getName())) {
				server.setName(getNewName(server.getName()));
			}
			server.setAttribute(IServerType.TYPE, LocalZendServerType.ID);
			final Server oldServer = ServersManager.getServer(server.getName());
			if (oldServer == null) {
				Server[] existingServers = ServersManager.getServers();
				String baseUrl = server.getBaseURL();
				for (Server existingServer : existingServers) {
					if (baseUrl.equals(existingServer.getBaseURL())) {
						return;
					}
				}
			}
			if (oldServer == null || oldServer.getPort() != server.getPort()) {
				if (oldServer != null) {
					ServersManager.removeServer(oldServer.getName());
				}
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						fetchServerData(server);
					}
				});

			}
		} else {
			NotificationManager.showWarningWithHelp(
					Messages.LocalZendServerStartup_NotFoundTitle,
					Messages.LocalZendServerStartup_NotFoundMessage,
					IHelpContextIds.ZEND_SERVER, 5000, MESSAGE_ID);
		}
	}

	/**
	 * Fetch local Zend server configuration data.
	 * 
	 * @param server
	 */
	private void fetchServerData(final Server server) {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell()) {
			@Override
			protected void configureShell(Shell shell) {
				super.configureShell(shell);
				shell.setText(Messages.LocalZendServerStartup_Local_zend_server_detector);
			}
		};
		try {
			progressDialog.run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.LocalZendServerStartup_Fetching_configuration, IProgressMonitor.UNKNOWN);
					monitor.subTask(Messages.LocalZendServerStartup_Detecting_webAPI_keys);
					LocalTargetDetector detector = new LocalTargetDetector(server);
					detector.detect();
					monitor.subTask(Messages.LocalZendServerStartup_Detecting_debugger_settings);
					// Detect debugger type if Web API is enabled
					IZendTarget zendTarget = detector.getFinalTarget();
					String debuggerId;
					if (zendTarget != null)
						debuggerId = DebugUtils.getDebuggerId(detector.getFinalTarget());
					else
						debuggerId = ServerTypeUtils.getLocalDebuggerId(server);
					server.setDebuggerId(debuggerId);
					// Set up best match IPs if it is Zend Debugger
					if (ZendDebuggerConfiguration.ID.equals(debuggerId)) {
						DebuggerSettingsManager debuggerSettingsManager = DebuggerSettingsManager.INSTANCE;
						IDebuggerSettings debuggerSettings = debuggerSettingsManager.findSettings(server.getUniqueId(),
								server.getDebuggerId());
						ZendDebuggerHostProposalComputer proposalsComputer = new ZendDebuggerHostProposalComputer();
						String ipsList = proposalsComputer.computeProposals(server);
						IDebuggerSettingsWorkingCopy debuggerSettingsWorkingCopy = debuggerSettingsManager
								.fetchWorkingCopy(debuggerSettings);
						debuggerSettingsWorkingCopy.setAttribute(ZendDebuggerSettingsConstants.PROP_CLIENT_IP, ipsList);
						debuggerSettingsManager.save(debuggerSettingsWorkingCopy);
						debuggerSettingsManager.dropWorkingCopy(debuggerSettingsWorkingCopy);
					}
					monitor.subTask(Messages.LocalZendServerStartup_Saving_configuration);
					ServersManager.addServer(server);
					if (ServersManager.getServers().length == 2) {
						// There is only an empty server and detected local Zend
						// Server
						ServersManager.setDefaultServer(null, server);
					}
					ServersManager.save();
					ZendServerManager.setupPathMapping(server);
					NotificationManager.showInfoWithHelp(
							Messages.LocalZendServerStartup_FoundTitle,
							Messages.LocalZendServerStartup_FoundMessage,
							IHelpContextIds.ZEND_SERVER, 5000);
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}

	}

	/**
	 * Generate unique server name by adding integer suffix to original name.
	 * 
	 * @param name
	 * @return unique server name
	 */
	private String getNewName(String name) {
		int suffix = 1;
		String newName = name + ' ' + suffix;
		while (!isUnique(newName)) {
			newName = name + ' ' + suffix;
			suffix++;
		}
		return newName;
	}

	/**
	 * Check if specified server name is unique.
	 * 
	 * @param name
	 * @return <code>true</code> if specified name is a unique server name;
	 *         otherwise return <code>false</code>
	 */
	private boolean isUnique(String name) {
		return ServersManager.getServer(name) == null;
	}

}
