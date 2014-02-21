package org.zend.php.zendserver.deployment.ui.actions;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ApiKeyDetector;
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
 * Detects localhost target
 * 
 */
public class DetectTargetAction extends Action {

	private IZendTarget target;

	private IStatus status;

	public DetectTargetAction() {
		super(Messages.DetectTargetAction_DetectTarget, Activator
				.getImageDescriptor(Activator.IMAGE_DETECT_TARGET));
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Throwable ex) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							ex.getMessage()), StatusManager.SHOW);
		}
	}

	public IStatus getStatus() {
		return status;
	}

	public void doRun() throws PrivilegesException {
		target = null;
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		try {
			// TODO handle a case when server has older version - elevate + default detection
			detectZendServer6(null);
			if (target != null || status.getSeverity() == IStatus.CANCEL
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
						"Target detection on Windows must always be run by privileged user."); //$NON-NLS-1$
			}
			try {
				target = tm.detectLocalhostTarget(null, null);
			} catch (IllegalArgumentException e) {
				detectZendServer6(null);
			}
		} catch (PrivilegesException e) {
			ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
			if (prog != null) {
				runElevated();
			} else {
				throw e;
			}
		} catch (DetectionException e) {
			Throwable cause = e.getCause();
			if (e instanceof ServerVersionException) {
				try {
					target.connect(WebApiVersion.V1_3, ServerType.ZEND_SERVER);
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
						Messages.DetectTargetAction_DetectUnsupportedTitle,
						Messages.DetectTargetAction_DetectUnsupportedDesc);
			}
		} catch (LicenseExpiredException e) {
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage());
			return;
		}

		if (target == null) {
			return;
		}

		if ((target.isTemporary()) && (EnvironmentUtils.isUnderLinux())) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			ZendDetectTargetCmdLine zcmd = new ZendDetectTargetCmdLine();
			String msg;
			try {
				msg = zcmd.getFullCommandLine(null, null);
			} catch (IOException e) {
				msg = e.getMessage() + Messages.DetectTargetAction_SeeDocs;
			}
			MessageDialog.openInformation(shell,
					Messages.DetectTargetAction_TargetDetected,
					Messages.DetectTargetAction_ToComplete + msg);
		}
	}

	public IZendTarget getDetectedTarget() {
		return target;
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
						target = tm.detectLocalhostTarget(id, key, true, false);
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
			} while (target == null && (tries-- > 0));
		}
	}

	private void detectZendServer6(final String message)
			throws DetectionException {
		// check if Zend Server is available at all
		try {
			new ZendTargetAutoDetect();
		} catch (IOException e) {
			this.status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
			return;
		}
		ApiKeyDetector manager = new EclipseApiKeyDetector();
		try {
			if (manager.createApiKey(message)) {
				TargetsManager tm = TargetsManagerService.INSTANCE
						.getTargetManager();
				this.status = Status.OK_STATUS;
				target = tm.detectLocalhostTarget(null, manager.getKey(),
						manager.getSecretKey());
			} else {
				status = Status.CANCEL_STATUS;
			}
		} catch (InvalidCredentialsException e) {
			detectZendServer6("Provided credentials are not valid."); //$NON-NLS-1$
		} catch (SdkException e) {
			String msg = e.getMessage();
			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			this.status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
			return;
		} catch (LicenseExpiredException e) {
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
			return;
		}
	}
}