/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.debug.core.debugger.DebuggerSettingsManager;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettings;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettingsWorkingCopy;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.LocalTargetDetector;
import org.zend.sdklib.internal.target.ZendTarget;
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
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalZendServerWizardFragment extends AbstractWizardFragment {

	@Override
	protected CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper) {
		return new LocalZendServerCompositeFragment(parent, wrapper, false);
	}

	@Override
	public boolean performFinish(IProgressMonitor monitor) throws CoreException {
		boolean result = super.performFinish(monitor);
		if (result) {
			monitor.beginTask(Messages.LocalZendServerCompositeFragment_DetectTitle, IProgressMonitor.UNKNOWN);
			
			monitor.subTask(Messages.LocalZendServerWizardFragment_FetchingConfiguration);
			Server server = null;
			try {
				server = ZendServerManager.getInstance().getLocalZendServer();
			} catch (DetectionException e) {
				Activator.logError(Messages.LocalZendServerCompositeFragment_CannotDetectError, e);
				setMessage(Messages.LocalZendServerCompositeFragment_CannotDetectError, IMessageProvider.ERROR);
				return composite.isComplete();
			}
			server.setName(this.server.getName());
			server.setAttribute(IServerType.TYPE, LocalZendServerType.ID);
			
			IZendTarget zendTarget = TargetsManagerService.INSTANCE.getTargetManager().getExistingLocalhost();
			if (zendTarget == null) {
				LocalTargetDetector detector = new LocalTargetDetector();
				detector.detect();
				zendTarget = detector.getFinalTarget();
			}
			if (zendTarget == null) {
				Activator.logError(Messages.LocalZendServerWizardFragment_NoLocalTargetFound_Error); 
				setMessage(Messages.LocalZendServerWizardFragment_NoLocalTargetFound_Error, IMessageProvider.ERROR);
				return composite.isComplete();
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
						// server document root folder has not been read from
						// the configuration
						VhostDetails vhostDetails = apiClient.vhostGetDetails(defaultVHostInfo.getId());
						String documentRoot = vhostDetails.getExtendedInfo().getDocRoot();
						server.setDocumentRoot(documentRoot);
					}
				} catch (MalformedURLException | WebApiException ex) {
					Activator.logError(Messages.LocalZendServerWizardFragment_UpdatingServerProperties_Error, ex);
					setMessage(Messages.LocalZendServerWizardFragment_UpdatingServerProperties_Error,
							IMessageProvider.ERROR);
					return composite.isComplete();
				}
			}

			monitor.subTask(Messages.LocalZendServerWizardFragment_DetectingDebuggerSettings);
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
			ZendServerManager.setupPathMapping(server);
			
			String targetServerName = zendTarget.getServerName();
			if(ServersManager.getServer(targetServerName) == null) {
				((ZendTarget) zendTarget).setServerName(server.getName());
				TargetsManagerService.INSTANCE.getTargetManager().updateTarget(zendTarget, true);
			}
			getWizardModel().putObject(WizardModel.SERVER, server);
			
			return composite.isComplete();
		}
		return result;
	}

}
