package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.targets.CreateTargetWizard;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * Adds new Deployment Target via TargetDetailsDialog.
 *
 */
public class AddTargetAction extends Action {
	
	private IZendTarget addedTarget;
	private String type;


	public AddTargetAction() {
		super(Messages.AddTargetAction_AddNewTarget, Activator.getImageDescriptor(Activator.IMAGE_ADD_TARGET));
	}

	
	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		CreateTargetWizard wizard = new CreateTargetWizard();
		if (type != null) {
			wizard.setType(type);
		}
		WizardDialog dialog = wizard.createDialog(window.getShell());
		
		if (dialog.open() != Window.OK) {
			return; // canceled by user
		}
		
		IZendTarget[] newTarget = wizard.getTarget();
		
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		Exception lastException = null;
		for (IZendTarget t : newTarget) {
			try {
				if (tm.getTargetById(t.getId()) != null
						|| tm.add(t, true) != null) {
					addedTarget = t;
				}
			} catch (TargetException e) {
				lastException = e;
			} catch (LicenseExpiredException e) {
				lastException = e;
			}
		}
		if (addedTarget == null && lastException != null) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, lastException.getMessage(), lastException), StatusManager.SHOW);
		}
	}

	/**
	 * @return Created target or null if target was not created.  
	 */
	public IZendTarget getTarget() {
		return addedTarget;
	}
	
	/**
	 * Set to customize target creation wizard. If it is set target type
	 * selection page is skipped.
	 * 
	 * @param type
	 *            of target which should be added
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}