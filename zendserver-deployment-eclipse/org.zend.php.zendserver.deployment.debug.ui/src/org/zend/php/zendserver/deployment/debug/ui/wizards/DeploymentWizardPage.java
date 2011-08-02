package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;

public abstract class DeploymentWizardPage extends WizardPage implements IStatusChangeListener {

	private IDeploymentHelper helper;

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
			setErrorMessage(status.getMessage());
			setPageComplete(false);
		}
	}

	protected IDeploymentHelper getHelper() {
		return helper;
	}

}
