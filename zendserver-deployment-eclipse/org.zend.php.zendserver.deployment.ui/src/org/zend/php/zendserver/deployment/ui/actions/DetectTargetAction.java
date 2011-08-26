package org.zend.php.zendserver.deployment.ui.actions;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.manager.PrivilegesException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

import swt.elevate.ElevatedProgram;
import swt.elevate.ElevatedProgramFactory;

/**
 * Detects localhost target
 *
 */
public class DetectTargetAction extends Action {
	
	private IZendTarget target;

	public DetectTargetAction() {
		super(Messages.DetectTargetAction_DetectTarget, Activator.getImageDescriptor(Activator.IMAGE_DETECT_TARGET));
	}

	@Override
	public void run() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		
		try {
			target = tm.detectLocalhostTarget(null, null);
		} catch (PrivilegesException e1) {
			runElevated();
		} catch (DetectionException e) {
			// do nothing
		}
		
		if ((target.isTemporary()) && (EnvironmentUtils.isUnderLinux())) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			ZendCmdLine zcmd = new ZendCmdLine();
			String msg;
			try {
				msg = zcmd.getFullCommandLine("detect target"); //$NON-NLS-1$
			} catch (IOException e) {
				msg = e.getMessage() + Messages.DetectTargetAction_SeeDocs;
			}
			MessageDialog.openInformation(shell, Messages.DetectTargetAction_TargetDetected, Messages.DetectTargetAction_ToComplete+msg);
		}
	}

	private void runElevated() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
		if (prog != null) {
			String id = tm.createUniqueId("local"); //$NON-NLS-1$
			ZendCmdLine zcmd = new ZendCmdLine();
			
			try {
				String key = TargetsManager.DEFAULT_KEY + "." + System.getProperty("user.name"); //$NON-NLS-1$ //$NON-NLS-2$
				if (! zcmd.runElevated("detect target -t "+id+" -k "+key)) { //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			} catch (IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e), StatusManager.LOG|StatusManager.SHOW);
				return;
			}
			
			/* 
			 * Elevated target detection may add new target to elevated user targets list, but
			 * all operations that required extra privileges should be done now, so let's try
			 * again to detect target.
			 */ 
			
			try {
				target = tm.detectLocalhostTarget(id, null);
			} catch (DetectionException e) {
				// ignore
			}
		}
	}

	public IZendTarget getDetectedTarget() {
		return target;
	}
}