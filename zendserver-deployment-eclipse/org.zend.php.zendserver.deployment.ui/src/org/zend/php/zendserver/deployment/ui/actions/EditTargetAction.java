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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.targets.CreateTargetWizard;
import org.zend.php.zendserver.deployment.ui.targets.DevCloudDetailsComposite;
import org.zend.php.zendserver.deployment.ui.targets.ZendTargetDetailsComposite;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * Opens editing dialog and updates the Target accordingly to user changes.
 */
public class EditTargetAction extends Action implements
		ISelectionChangedListener {

	private ISelectionProvider provider;
	private boolean isEnabled;
	private ISelection selection;

	public EditTargetAction(ISelectionProvider provider) {
		super(Messages.EditTargetAction_EditTarget, Activator
				.getImageDescriptor(Activator.IMAGE_EDIT_TARGET));
		this.provider = provider;
		provider.addSelectionChangedListener(this);
	}
	
	public EditTargetAction(ISelection selection) {
		super(Messages.EditTargetAction_EditTarget, Activator
				.getImageDescriptor(Activator.IMAGE_EDIT_TARGET));
		this.selection = selection;
	}

	@Override
	public void run() {
		ISelection selection = null;
		if (this.provider != null) {
			selection = provider.getSelection();
		}
		
		if (this.selection != null) {
			selection = this.selection;
		}
		
		if (selection == null || selection.isEmpty()) {
			return;
		}

		IStructuredSelection ssel = (IStructuredSelection) selection;
		Object obj = ssel.getFirstElement();

		if (!(obj instanceof IZendTarget)) {
			return;
		}
		IZendTarget toEdit = (IZendTarget) obj;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		CreateTargetWizard ctw = new CreateTargetWizard();
		ctw.setType(getTargetType(toEdit));
		ctw.setDefaultTarget(toEdit);
		WizardDialog dialog = ctw.createDialog(window.getShell());
		ctw.setWindowTitle(Messages.EditTargetAction_EditTarget);

		if (dialog.open() != Window.OK) {
			return; // canceled by user
		}

		
		IZendTarget[] newTargets = ctw.getTarget();
		if (newTargets == null) {
			return; // validation error while editing target
		}

		// what happens for multiple case?
		IZendTarget newTarget = null;  
		String origContainerName = toEdit.getProperty(ZendDevCloud.TARGET_CONTAINER);
		if (origContainerName != null) {
			for (IZendTarget t : newTargets) {
				String containerName = t.getProperty(ZendDevCloud.TARGET_CONTAINER);
				if (origContainerName.equals(containerName)) {
					newTarget = t;
				}
			}	
		} else {
			newTarget = newTargets.length > 0 ? newTargets[0] : null;
		}
		
		if (newTarget == null) {
			return; // validation error while editing target
		}
		
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		
		String defaultServer = newTarget.getDefaultServerURL() != null ? newTarget.getDefaultServerURL().toString() : null;
		String host = newTarget.getHost() != null ? newTarget.getHost().toString() : null;

		try {
			tm.updateTarget(toEdit.getId(), host, defaultServer,
					newTarget.getKey(), newTarget.getSecretKey());
		} catch (LicenseExpiredException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage()), StatusManager.SHOW);
			return;
		}
		updateTargetProperties(toEdit, newTarget);
	}

	private void updateTargetProperties(IZendTarget dest,
			IZendTarget src) {
		ZendTarget zsSrc = (ZendTarget) src;
		ZendTarget zsDest = (ZendTarget) dest;
		
		String[] keys = zsSrc.getPropertiesKeys();
		for (String key : keys) {
			String newValue = zsSrc.getProperty(key);
			zsDest.addProperty(key, newValue);
		}
	}

	private String getTargetType(IZendTarget toEdit) {
		if ((toEdit != null) && (toEdit.getProperty(ZendDevCloud.TARGET_CONTAINER) != null)) {
			return DevCloudDetailsComposite.class.getName();
		}
		
		return ZendTargetDetailsComposite.class.getName();
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