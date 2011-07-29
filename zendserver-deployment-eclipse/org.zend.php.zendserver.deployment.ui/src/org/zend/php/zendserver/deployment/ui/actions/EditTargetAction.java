package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
 * Opens editing dialog and updates the Target accordingly to user changes.
 */
public class EditTargetAction extends Action implements ISelectionChangedListener {
	
	private ISelectionProvider provider;
	private boolean isEnabled;

	public EditTargetAction(ISelectionProvider provider) {
		super(Messages.EditTargetAction_EditTarget, Activator.getImageDescriptor(Activator.IMAGE_EDIT_TARGET));
		this.provider = provider;
		provider.addSelectionChangedListener(this);
	}

	@Override
	public void run() {
		ISelection selection = provider.getSelection();
		if (selection.isEmpty()) {
			return;
		}
		
		IStructuredSelection ssel = (IStructuredSelection) selection;
		Object obj = ssel.getFirstElement();
		
		if (! (obj instanceof IZendTarget)) {
			return;
		}
		IZendTarget toEdit = (IZendTarget) obj;
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		TargetDialog dialog = new TargetDialog(window.getShell());
		dialog.setMessage(Messages.EditTargetAction_EditTargetMessage);
		dialog.setTitle(Messages.EditTargetAction_EditTarget);
		dialog.setDefaultTarget(toEdit);
		
		if (dialog.open() != Window.OK) {
			return; // canceled by user
		}
		
		IZendTarget newTarget = dialog.getTarget();
		
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		try {
			tm.updateTarget(toEdit.getId(), newTarget.getHost().toString(), newTarget.getKey(), newTarget.getSecretKey());
			tm.add(newTarget);
		} catch (WebApiException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e), StatusManager.SHOW);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty() == isEnabled) {
			isEnabled = !isEnabled;
			firePropertyChange(ENABLED, !isEnabled, isEnabled);
		}
		
	}
}