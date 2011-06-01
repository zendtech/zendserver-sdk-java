package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.Activator;


public class ExportApplicationAction extends Action {

	public ExportApplicationAction() {
		setText("Export");
		setToolTipText("Export application package");
		setImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_EXPORT_APPLICATION));
	}
	
	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openInformation(shell, "Export application", "Not ready yet!");
		// TODO
	}
	
}
