package org.zend.php.zendserver.deployment.ui.actions;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
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
		
		String id = tm.createUniqueId("local"); //$NON-NLS-1$
		ZendCmdLine zcmd = new ZendCmdLine();
		
		ElevatedProgram prog = ElevatedProgramFactory.getElevatedProgram();
		if (prog != null) {
			try {
				if (! zcmd.runElevated("detect target -t "+id)) { //$NON-NLS-1$
					return;
				}
			} catch (IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e), StatusManager.LOG|StatusManager.SHOW);
				return;
			}
			
			// use new TargetManager to force re-load targets and check if new target was added
			TargetsManager newTm = new TargetsManager(); 
			IZendTarget newTarget = newTm.getTargetById(id);
			if (newTarget != null) {
				newTm.remove(newTarget); // remove temporary target.
			}
			
			this.target = newTarget;
		} else {
			tm.detectLocalhostTarget(null, null);
		}
	}

	public IZendTarget getDetectedTarget() {
		return target;
	}
}