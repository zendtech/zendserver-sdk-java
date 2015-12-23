package org.zend.php.zendserver.deployment.ui;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.php.internal.debug.core.debugger.DebuggerSettingsManager;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettings;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettingsWorkingCopy;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
import org.zend.php.zendserver.deployment.ui.notifications.AddingLocalZendServerNotification;
import org.zend.php.zendserver.deployment.ui.notifications.base.NotificationHelper;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.VhostDetails;
import org.zend.webapi.core.connection.data.VhostInfo;
import org.zend.webapi.core.connection.data.VhostsList;

@SuppressWarnings("restriction")
public class AddLocalZendServerJob extends Job {

	public AddLocalZendServerJob() {
		super(Messages.AddLocalZendServerJob_JobName);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(Messages.AddLocalZendServerJob_AddingServer_TaskName, IProgressMonitor.UNKNOWN);
		try {
			Server server = null;
			try {
				server = ZendServerManager.getInstance().getLocalZendServer();
			} catch (DetectionException ex) {
				String message = Messages.AddLocalZendServerJob_NoLocalInstallationFound_Message;
				Activator.logError(message, ex);
				notifyWarning(message);
				return Status.CANCEL_STATUS;
			}

			IZendTarget zendTarget = TargetsManagerService.INSTANCE.getTargetManager().getExistingLocalhost();
			if (zendTarget == null) {
				monitor.subTask(Messages.AddLocalZendServerJob_DetectingWebAPIKeys);
				LocalTargetDetector detector = new LocalTargetDetector();
				detector.detect();
				zendTarget = detector.getFinalTarget();
			}
			if (zendTarget == null) {
				String message = Messages.AddLocalZendServerJob_NoLocalTargetFound_Message;
				Activator.logError(message);
				notifyWarning(message);
				return Status.CANCEL_STATUS;
			}

			monitor.subTask(Messages.AddLocalZendServerJob_UpdatingServerProperties);
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
					String message = Messages.AddLocalZendServerJob_CouldNotObtainAllProperties_Message;
					Activator.logError(message, ex);
					notifyWarning(message);
					return Status.CANCEL_STATUS;
				}
			}
			monitor.subTask(Messages.AddLocalZendServerJob_DetectingDebuggerSettings);
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

			monitor.subTask(Messages.AddLocalZendServerJob_SavingConfiguration);
			ServersManager.addServer(server);
			if (ServersManager.getServers().length == 2) {
				// There is only an empty server and detected local Zend
				// Server
				ServersManager.setDefaultServer(null, server);
			}
			ServersManager.save();

			ZendServerManager.setupPathMapping(server);
			showPhpServersView();
			notify(Messages.AddLocalZendServerJob_AddingServerSucceeded_Title,
					Messages.AddLocalZendServerJob_ServerAdded_message,
					AddingLocalZendServerNotification.NotificationTypes.INFORMATION);
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private boolean isUnique(String name) {
		return ServersManager.getServer(name) == null;
	}

	private String getNewName(String name) {
		int suffix = 1;
		String newName = name + ' ' + suffix;
		while (!isUnique(newName)) {
			newName = name + ' ' + suffix;
			suffix++;
		}
		return newName;
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

	private void notifyWarning(String message) {
		notify(Messages.AddLocalZendServerJob_AddingServerFailed_Title, message,
				AddingLocalZendServerNotification.NotificationTypes.WARNING);
	}

	private void notify(String label, String message, AddingLocalZendServerNotification.NotificationTypes type) {
		AddingLocalZendServerNotification notification = new AddingLocalZendServerNotification(type);
		notification.setDescription(message);
		notification.setLabel(label);
		NotificationHelper.notify(notification);
	}
}
