/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui;

import java.net.MalformedURLException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.debug.core.debugger.DebuggerSettingsManager;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettings;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettingsWorkingCopy;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.php.server.core.types.ServerTypesManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.server.ui.IHelpContextIds;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.VhostDetails;
import org.zend.webapi.core.connection.data.VhostInfo;
import org.zend.webapi.core.connection.data.VhostsList;

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
	private static final String LOCALHOST_BASE_URL = "http://localhost"; //$NON-NLS-1$

	private class AddServerConfirmationRunnable implements Runnable {
		boolean willDetectWebApi = true;
		boolean addServer = false;

		public AddServerConfirmationRunnable(boolean willDetectWebApi) {
			this.willDetectWebApi = willDetectWebApi;
		}

		@Override
		public void run() {
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String message = Messages.LocalZendServerStartup_ServerDetectedMessage;
			if (willDetectWebApi)
				message = Messages.LocalZendServerStartup_ServerDetectedWebApiMessage;
			addServer = MessageDialog.openConfirm(shell, Messages.LocalZendServerStartup_LocalZendServer, message);
		}

	}

	@Override
	public void earlyStartup() {
		fetchServerData();
	}

	private void fetchServerData() {
		Job performer = new Job(Messages.LocalZendServerStartup_RegisteringZendServer) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.LocalZendServerStartup_RegisteringZendServer, IProgressMonitor.UNKNOWN);
				
				//look for existing local server; it does not necessarily have to be the default one
				monitor.subTask(Messages.LocalZendServerStartup_CheckingExistingServers);
				ServerTypesManager typesManager = ServerTypesManager.getInstance();
				Server[] servers = ServersManager.getServers();
				for (Server server : servers) {
					IServerType serverType = typesManager.getType(server);
					if(LocalZendServerType.ID.equalsIgnoreCase(serverType.getId())) {
						Activator.logInfo(
								MessageFormat.format(Messages.LocalZendServerStartup_LocalZendServerExists_Info,
										server.getName()));
						return Status.OK_STATUS;
					}
				}
				
				monitor.subTask(Messages.LocalZendServerStartup_FetchingConfiguration);
				final Server server;
				try {
					server = ZendServerManager.getInstance().getLocalZendServer();
				} catch (DetectionException e) {
					NotificationManager.showWarningWithHelp(Messages.LocalZendServerStartup_NotFoundTitle,
							Messages.LocalZendServerStartup_NotFoundMessage, IHelpContextIds.ZEND_SERVER, 5000,
							MESSAGE_ID);
					return Status.CANCEL_STATUS;
				}
				server.setAttribute(IServerType.TYPE, LocalZendServerType.ID);

				IZendTarget zendTarget = TargetsManagerService.INSTANCE.getTargetManager().getExistingLocalhost();
				AddServerConfirmationRunnable run1 = new AddServerConfirmationRunnable(zendTarget == null);
				Display.getDefault().syncExec(run1);
				if (!run1.addServer) {
					Activator.logInfo(Messages.LocalZendServerStartup_DetectingLocalZendServerSkipped_Message);
					return Status.CANCEL_STATUS;
				}

				if (zendTarget == null) {
					monitor.subTask(Messages.LocalZendServerStartup_DetectingWebAPIKeys);
					LocalTargetDetector detector = new LocalTargetDetector();
					detector.detect();
					zendTarget = detector.getFinalTarget();
				}
				if (zendTarget == null) {
					reportNullLocalTarget(new DetectionException(Messages.LocalZendServerStartup_NoLocalTargetFound_Error));
					return Status.CANCEL_STATUS;
				}

				monitor.subTask(Messages.LocalZendServerStartup_UpdatingServerProperties);
				if (!isUnique(server.getName())) {
					server.setName(getNewName(server.getName()));
				}

				if (server.getBaseURL() == "" || server.getDocumentRoot() == "") { //$NON-NLS-1$ //$NON-NLS-2$
					try {
						VhostInfo defaultVHostInfo = null;
						WebApiCredentials credentials = new BasicCredentials(zendTarget.getKey(),
								zendTarget.getSecretKey());
						WebApiClient apiClient = new WebApiClient(credentials, zendTarget.getHost().toString());
						apiClient.setServerType(zendTarget.getServerType());
						VhostsList vhostsList = apiClient.vhostGetStatus();
						for (VhostInfo vhostInfo : vhostsList.getVhosts()) {
							if (!vhostInfo.isDefaultVhost())
								continue;

							defaultVHostInfo = vhostInfo;
						}

						if (server.getBaseURL() == "" && defaultVHostInfo != null) { //$NON-NLS-1$
							// server base URL has not been read from
							// the configuration
							String baseUrl = "http://localhost:" + Integer.toString(defaultVHostInfo.getPort()); //$NON-NLS-1$
							server.setBaseURL(baseUrl);
						}

						if (server.getDocumentRoot() == "" && defaultVHostInfo != null) { //$NON-NLS-1$
							// server document root folder has not been read
							// from
							// the configuration
							VhostDetails vhostDetails = apiClient.vhostGetDetails(defaultVHostInfo.getId());
							String documentRoot = vhostDetails.getExtendedInfo().getDocRoot();
							server.setDocumentRoot(documentRoot);
						}
					} catch (MalformedURLException | WebApiException ex) {
						Activator.logError(Messages.LocalZendServerStartup_UpdatingServerProperties_Error, ex);
						NotificationManager.showWarningWithHelp(Messages.LocalZendServerStartup_NotFoundTitle,
								Messages.LocalZendServerStartup_CouldNotObtainAllProperties,
								IHelpContextIds.ZEND_SERVER, 5000, MESSAGE_ID);
						return Status.CANCEL_STATUS;
					}
				}

				monitor.subTask(Messages.LocalZendServerStartup_DetectingDebuggerSettings);
				// Detect debugger type if Web API is enabled
				String debuggerId = DebugUtils.getDebuggerId(zendTarget);
				server.setDebuggerId(debuggerId);
				// Set up best match IP (localhost only) if it is Zend Debugger
				if (ZendDebuggerConfiguration.ID.equals(debuggerId)) {
					DebuggerSettingsManager debuggerSettingsManager = DebuggerSettingsManager.INSTANCE;
					IDebuggerSettings debuggerSettings = debuggerSettingsManager.findSettings(server.getUniqueId(),
							server.getDebuggerId());
					IDebuggerSettingsWorkingCopy debuggerSettingsWorkingCopy = debuggerSettingsManager
							.fetchWorkingCopy(debuggerSettings);
					debuggerSettingsWorkingCopy.setAttribute(ZendDebuggerSettingsConstants.PROP_CLIENT_IP, "127.0.0.1"); //$NON-NLS-1$
					debuggerSettingsManager.save(debuggerSettingsWorkingCopy);
					debuggerSettingsManager.dropWorkingCopy(debuggerSettingsWorkingCopy);
				}

				monitor.subTask(Messages.LocalZendServerStartup_SavingConfiguration);
				ServersManager.addServer(server);
				if (ServersManager.getServers().length == 2) {
					// There is only an empty server and detected local Zend
					// Server
					ServersManager.setDefaultServer(null, server);
				}
				ServersManager.save();

				ZendServerManager.setupPathMapping(server);
				showPhpServersView();
				NotificationManager.showInfoWithHelp(Messages.LocalZendServerStartup_FoundTitle,
						Messages.LocalZendServerStartup_FoundMessage, IHelpContextIds.ZEND_SERVER, 5000);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		performer.setUser(false);
		performer.setSystem(false);
		performer.schedule();
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

	private void reportNullLocalTarget(Throwable e) {
		Activator.logError(Messages.LocalZendServerStartup_DetectingLocalZendServer_Error, e);
		NotificationManager.showWarningWithHelp(Messages.LocalZendServerStartup_NotFoundTitle,
				Messages.LocalZendServerStartup_NoLocalTargetFound_Warning, IHelpContextIds.ZEND_SERVER,
				5000, MESSAGE_ID);
	}

	private void showPhpServersView() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.zend.php.server.ui.views.ServersView"); //$NON-NLS-1$
				} catch (PartInitException e) {
					Activator.log(e);
				}
			}
		});
	}

}
