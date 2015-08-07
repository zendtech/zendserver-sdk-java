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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.debug.core.debugger.DebuggerSettingsManager;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettings;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerSettingsWorkingCopy;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.zend.debugger.ZendDebuggerSettingsConstants;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;
import org.zend.php.server.ui.types.ServerTypeUtils;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.debug.core.DebugUtils;
import org.zend.php.zendserver.deployment.ui.LocalTargetDetector;
import org.zend.sdklib.target.IZendTarget;

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
			monitor.beginTask(
					Messages.LocalZendServerCompositeFragment_DetectTitle,
					IProgressMonitor.UNKNOWN);
			final Server server = ZendServerManager.getInstance()
					.getLocalZendServer(this.server);
			if (server != null) {
				String location = server.getAttribute(
						ZendServerManager.ZENDSERVER_INSTALL_LOCATION, null);
				if (location == null || location.isEmpty()) {
					setMessage(
							Messages.LocalZendServerCompositeFragment_CannotDetectError,
							IMessageProvider.ERROR);
				} else {
					ZendServerManager.setupPathMapping(server);
					LocalTargetDetector detector = new LocalTargetDetector(
							this.server);
					detector.detect();
					// Detect debugger type if Web API is enabled
					IZendTarget zendTarget = detector.getFinalTarget();
					String debuggerId;
					if (zendTarget != null)
						debuggerId = DebugUtils.getDebuggerId(detector.getFinalTarget());
					else
						debuggerId = ServerTypeUtils.getLocalDebuggerId(server);
					server.setDebuggerId(debuggerId);
					// Set up best match IP (localhost only) if it is Zend Debugger
					if (ZendDebuggerConfiguration.ID.equals(debuggerId)) {
						DebuggerSettingsManager debuggerSettingsManager = DebuggerSettingsManager.INSTANCE;
						IDebuggerSettings debuggerSettings = debuggerSettingsManager.findSettings(server.getUniqueId(), server.getDebuggerId());
						IDebuggerSettingsWorkingCopy debuggerSettingsWorkingCopy = debuggerSettingsManager.fetchWorkingCopy(debuggerSettings);
						debuggerSettingsWorkingCopy.setAttribute(ZendDebuggerSettingsConstants.PROP_CLIENT_IP, "127.0.0.1"); //$NON-NLS-1$
						debuggerSettingsManager.save(debuggerSettingsWorkingCopy);
						debuggerSettingsManager.dropWorkingCopy(debuggerSettingsWorkingCopy);
					}
					if (detector.getStatus().getSeverity() != IStatus.OK && detector.getStatus().getSeverity() != IStatus.CANCEL) {
						setMessage(detector.getStatus().getMessage(),
								IMessageProvider.ERROR);
					}
				}
			} else {
				setMessage(
						Messages.LocalZendServerCompositeFragment_CannotDetectError,
						IMessageProvider.ERROR);
			}
			return composite.isComplete();
		}
		return result;
	}

}
