package org.zend.php.library.internal.ui.preferences;

import java.util.List;

import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler;

public class DeployButtonHandler implements IPHPLibraryButtonHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getLabel()
	 */
	public String getLabel() {
		return "Deploy to Target";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getPosition()
	 */
	public int getPosition() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#handleSelection
	 * (org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField)
	 */
	public void handleSelection(TreeListDialogField field) {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#selectionChanged
	 * (org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField)
	 */
	public boolean selectionChanged(TreeListDialogField field) {
		List selected = field.getSelectedElements();
		return selected.size() == 1
				&& selected.get(0) instanceof BPUserLibraryElement;
	}

}
