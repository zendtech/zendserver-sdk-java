/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.ZendDetectTargetCmdLine;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ApiKeyDetector;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.manager.PrivilegesException;
import org.zend.sdklib.manager.ServerVersionException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.InvalidCredentialsException;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.internal.core.connection.exception.InvalidResponseException;

import swt.elevate.ElevatedProgram;
import swt.elevate.ElevatedProgramFactory;

/**
 * Class responsible for detection of local Zend Server target. It should be
 * used as a part of local Zend Server detection process.
 * 
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalTargetDetector {

	private IZendTarget finalTarget;
	private IStatus status;

	private Server server;

	public LocalTargetDetector(Server server) {
		this.server = server;
	}

	public IZendTarget getFinalTarget() {
		return finalTarget;
	}

	public IStatus getStatus() {
		return status;
	}

	public void detect() {
		try {
			status = Status.OK_STATUS;
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			try {
				// TODO handle a case when server has older version - elevate +
				// default detection
				finalTarget = detectZendServer6(null);
				if (finalTarget != null
						|| status.getSeverity() == IStatus.CANCEL
						|| status.getSeverity() == IStatus.ERROR) {
					return;
				}
				// test if VirtualStore is enabled, and enforce elevated target
				// creation,
				// because non-elevated writes ZendServer configuration to
				// VirtualStore,
				// where it's never read by ZendServer
				if (EnvironmentUtils.isUnderWindows()
						&& EnvironmentUtils.isUACEnabled()) {
					throw new PrivilegesException(
							Messages.LocalTargetDetector_WindowsPriviligesMessage);
				}
				try {
					finalTarget = manager.detectLocalhostTarget(null, null);
				} catch (IllegalArgumentException e) {
					finalTarget = detectZendServer6(null);
				}
			} catch (PrivilegesException e) {
				ElevatedProgram prog = ElevatedProgramFactory
						.getElevatedProgram();
				if (prog != null) {
					runElevated();
				} else {
					status = getError(e.getMessage());
				}
			} catch (DetectionException e) {
				Throwable cause = e.getCause();
				if (e instanceof ServerVersionException) {
					try {
						finalTarget.connect(WebApiVersion.V1_3,
								ServerType.ZEND_SERVER);
					} catch (WebApiException ex) {
						cause = ex.getCause();
					} catch (LicenseExpiredException ex) {
						cause = ex.getCause();
					}
				}
				if (cause instanceof InvalidResponseException) {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					MessageDialog.openInformation(shell,
							Messages.LocalTargetDetector_UnsupportedTitle,
							Messages.LocalTargetDetector_UnsupportedMessage);
				}
			} catch (LicenseExpiredException e) {
				status = getError(e.getMessage());
				return;
			}

			if (finalTarget == null) {
				return;
			}

			if ((finalTarget.isTemporary())
					&& (EnvironmentUtils.isUnderLinux())) {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				ZendDetectTargetCmdLine zcmd = new ZendDetectTargetCmdLine();
				String msg;
				try {
					msg = zcmd.getFullCommandLine(null, null);
				} catch (IOException e) {
					msg = e.getMessage()
							+ Messages.LocalTargetDetector_SeeDocMessage;
				}

				MessageDialog.openInformation(shell,
						Messages.LocalTargetDetector_CompleteTitle,
						Messages.LocalTargetDetector_CompleteMessage + msg);
			}
		} finally {
			if (finalTarget != null) {
				ZendTarget t = (ZendTarget) finalTarget;
				try {
					t.setDefaultServerURL(new URL(server.getBaseURL()));
					if (finalTarget.getServerName() == null) {
						t.setServerName(server.getName());
					}
					finalTarget = TargetsManagerService.INSTANCE
							.getTargetManager().updateTarget(t, true);
				} catch (MalformedURLException e) {
					// should not occur
				}
			}
		}
	}

	private void runElevated() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
		if (prog != null) {
			String id = tm.createUniqueId("local"); //$NON-NLS-1$
			ZendDetectTargetCmdLine zcmd = new ZendDetectTargetCmdLine();

			String key = TargetsManager.DEFAULT_KEY
					+ "." + System.getProperty("user.name"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				if (!zcmd.runElevated(id, key)) {
					return;
				}
			} catch (IOException e) {
				StatusManager.getManager().handle(
						new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								e.getMessage(), e),
						StatusManager.LOG | StatusManager.SHOW);
				return;
			}

			/*
			 * Elevated target detection may add new target to elevated user
			 * targets list, but all operations that required extra privileges
			 * should be done now, so let's try again to detect target.
			 */

			int maxtries = 3;
			int tries = maxtries;
			do {
				if (tries < maxtries) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// empty
					}
				}
				try {
					try {
						finalTarget = tm.detectLocalhostTarget(id, key, true,
								false);
					} catch (IllegalArgumentException e) {
						detectZendServer6(null);
					} catch (DetectionException e) {
						detectZendServer6(null);
					} catch (LicenseExpiredException e) {
						status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								e.getMessage());
						return;
					}
				} catch (DetectionException e) {
					// ignore
				}
			} while (finalTarget == null && (tries-- > 0));
		}
	}

	private IZendTarget detectZendServer6(final String message)
			throws DetectionException {
		// check if Zend Server is available at all
		try {
			new ZendTargetAutoDetect();
		} catch (IOException e) {
			status = getError(e.getMessage(), e);
			return null;
		}
		ApiKeyDetector manager = new EclipseApiKeyDetector();
		try {
			if (manager.createApiKey(message)) {
				TargetsManager tm = TargetsManagerService.INSTANCE
						.getTargetManager();
				return tm.detectLocalhostTarget(null, manager.getKey(),
						manager.getSecretKey());
			} else {
				status = Status.CANCEL_STATUS;
			}
		} catch (InvalidCredentialsException e) {
			return detectZendServer6(Messages.LocalTargetDetector_InvalidCredentialsMessage);
		} catch (SdkException e) {
			String msg = e.getMessage();
			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			status = getError(msg, e);
		} catch (LicenseExpiredException e) {
			status = getError(e.getMessage(), e);
		}
		return null;
	}

	private IStatus getError(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	private IStatus getError(String message, Exception e) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
	}

}
