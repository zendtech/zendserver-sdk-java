package org.zend.php.zendserver.deployment.ui.actions;

import java.io.IOException;
import java.text.MessageFormat;

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
			String msg = MessageFormat.format("sudo java -jar org.zend.sdk.jar org.zend.sdkcli.Main detect target -k {0} -s {1}", target.getKey(), target.getSecretKey());
			MessageDialog.openInformation(shell, "Target detected", "To complete adding local target, please run the following command:\n"+msg);
		}
	}

	private void runElevated() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
		if (prog != null) {
			String id = tm.createUniqueId("local"); //$NON-NLS-1$
			ZendCmdLine zcmd = new ZendCmdLine();
			
			try {
				String key = TargetsManager.DEFAULT_KEY + "." + System.getProperty("user.name");
				if (! zcmd.runElevated("detect target -t "+id+" -k "+key)) { //$NON-NLS-1$
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