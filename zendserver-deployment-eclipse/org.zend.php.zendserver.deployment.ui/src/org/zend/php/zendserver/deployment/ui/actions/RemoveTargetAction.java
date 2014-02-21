package org.zend.php.zendserver.deployment.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Removes target. Uses ISelectionProvider to obtain the target to remove.
 *
 */
public class RemoveTargetAction extends Action implements ISelectionChangedListener {
	
	private ISelectionProvider provider;
	private boolean isEnabled;
	
	public RemoveTargetAction(ISelectionProvider provider) {
		super(Messages.RemoveTargetAction_RemoveTarget, Activator.getImageDescriptor(Activator.IMAGE_REMOVE_TARGET));
		setDisabledImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_REMOVE_TARGET_DISABLED));
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
		
		for (Iterator i = ssel.iterator(); i.hasNext(); ) {
			Object obj = i.next();
			if (obj instanceof IZendTarget) {
				IZendTarget target = (IZendTarget) obj;
				TargetsManagerService.INSTANCE.removeTarget(target);
				MonitorManager.removeTargetMonitor(target.getId());
			}
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