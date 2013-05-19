package org.zend.php.library.internal.ui.preferences;

import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler;
import org.eclipse.swt.widgets.Display;
import org.zend.php.library.internal.ui.wizards.ImportZpkWizard;

public class ImportZpkHandler implements IPHPLibraryButtonHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getLabel()
	 */
	public String getLabel() {
		return "Import form ZPK";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getPosition()
	 */
	public int getPosition() {
		return 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#handleSelection
	 * (org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField)
	 */
	public void handleSelection(final TreeListDialogField field) {
		ImportZpkWizard wizard = new ImportZpkWizard();
		WizardDialog dialog = new WizardDialog(field.getTreeViewer().getTree()
				.getShell(), wizard);
		if (dialog.open() == Window.OK) {
			BPUserLibraryElement element = wizard.getElement();
			if (element != null) {
				field.addElement(element);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						field.refresh();
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#selectionChanged
	 * (org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField)
	 */
	public boolean selectionChanged(TreeListDialogField field) {
		return true;
	}

}
