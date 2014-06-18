package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.wizards.PackageExportWizard;

public class ExportApplicationAction extends Action {

	private IProject project;

	public ExportApplicationAction() {
		setText(Messages.ExportApplicationAction_0);
		setToolTipText(Messages.ExportApplicationAction_1);
		setImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_EXPORT_APPLICATION));
	}

	public ExportApplicationAction(IProject project) {
		this();
		this.project = project;
	}

	@Override
	public void run() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		Shell shell = workbench.getActiveWorkbenchWindow().getShell();

		PackageExportWizard wizard = new PackageExportWizard();
		wizard.init(workbench, new StructuredSelection(project));

		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		dialog.open();
	}

}
