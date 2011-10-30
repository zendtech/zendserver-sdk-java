package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

public abstract class DeploymentWizardPage extends WizardPage implements IStatusChangeListener {

	protected IDeploymentHelper helper;

	protected DeploymentWizardPage(String pageName, IDeploymentHelper helper) {
		super(pageName);
		this.helper = helper;
	}

	public void statusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			setMessage(status.getMessage());
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			if (status.getSeverity() == IStatus.ERROR) {
				setErrorMessage(status.getMessage());
			} else {
				setErrorMessage(null);
			}
			setPageComplete(false);
		}
	}

	public abstract IDeploymentHelper getHelper();

}
