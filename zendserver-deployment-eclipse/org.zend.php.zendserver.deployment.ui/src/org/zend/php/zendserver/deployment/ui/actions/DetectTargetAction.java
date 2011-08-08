package org.zend.php.zendserver.deployment.ui.actions;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
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
	}

	private void runElevated() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
		if (prog != null) {
			String id = tm.createUniqueId("local"); //$NON-NLS-1$
			ZendCmdLine zcmd = new ZendCmdLine();
			
			try {
				if (! zcmd.runElevated("detect target -t "+id)) { //$NON-NLS-1$
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