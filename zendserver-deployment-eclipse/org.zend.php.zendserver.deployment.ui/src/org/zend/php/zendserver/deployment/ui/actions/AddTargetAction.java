package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.targets.TargetDialog;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

/**
 * Adds new Deployment Target via TargetDialog.
 *
 */
public class AddTargetAction extends Action {
	
	private IZendTarget addedTarget;


	public AddTargetAction() {
		super(Messages.AddTargetAction_AddNewTarget, Activator.getImageDescriptor(Activator.IMAGE_ADD_TARGET));
	}

	
	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		TargetDialog dialog = new TargetDialog(window.getShell());
		dialog.setMessage(Messages.AddTargetAction_AddTargetMessage);
		dialog.setTitle(Messages.AddTargetAction_AddTarget);
		
		if (dialog.open() != Window.OK) {
			return; // canceled by user
		}
		
		IZendTarget newTarget = dialog.getTarget();
		
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		try {
			tm.add(newTarget);
			addedTarget = newTarget;
		} catch (WebApiException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e), StatusManager.SHOW);
		}
	}

	/**
	 * @return Created target or null if target was not created.  
	 */
	public IZendTarget getTarget() {
		return addedTarget;
	}
}