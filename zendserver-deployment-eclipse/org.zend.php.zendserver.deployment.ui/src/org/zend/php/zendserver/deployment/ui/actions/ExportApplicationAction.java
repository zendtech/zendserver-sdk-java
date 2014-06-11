package org.zend.php.zendserver.deployment.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		PackageExportWizard wizard = new PackageExportWizard();
		List<IProject> selection = new ArrayList<IProject>();
		selection.add(project);
		wizard.setInitialSelection(selection);
		wizard.setWindowTitle(Messages.ExportApplicationAction_2);
		WizardDialog dialog = createDialog(window.getShell(), wizard);
		dialog.open();
	}

	/**
	 * Utility method to create typical wizard dialog for
	 * ZendStudioFirstStartWizard
	 * 
	 * @param parent
	 * @param wizard
	 * @return
	 */
	public static WizardDialog createDialog(Shell parent,
			PackageExportWizard wizard) {
		WizardDialog dialog = new WizardDialog(parent, wizard);
		dialog.setHelpAvailable(false);
		dialog.create();

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = parent.getBounds();
		Point dialogSize = dialog.getShell().getSize();

		dialog.getShell().setLocation(
				shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
				shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

		return dialog;
	}

}
