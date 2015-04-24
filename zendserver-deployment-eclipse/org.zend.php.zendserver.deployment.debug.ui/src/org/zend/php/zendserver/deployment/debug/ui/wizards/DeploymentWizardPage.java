package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

public abstract class DeploymentWizardPage extends WizardPage implements
		IStatusChangeListener {

	protected IDeploymentHelper helper;
	private String help;

	protected DeploymentWizardPage(String pageName, IDeploymentHelper helper,
			String help) {
		super(pageName);
		this.helper = helper;
		this.help = help;
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

	public void createControl(Composite parent) {
		if (help != null) {
			parent.setData(WorkbenchHelpSystem.HELP_KEY, help);
			parent.addHelpListener(new HelpListener() {
				public void helpRequested(HelpEvent arg0) {
					org.eclipse.swt.program.Program.launch(help);
				}
			});
		}
	}

	public abstract IDeploymentHelper getHelper();

}
