package org.zend.php.library.internal.ui.preferences;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler;
import org.zend.php.library.internal.ui.wizards.LibraryDeploymentUtils;

public class DeployButtonHandler implements IPHPLibraryButtonHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getLabel()
	 */
	public String getLabel() {
		return Messages.DeployButtonHandler_0;
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
		List elements = field.getSelectedElements();
		File root = null;
		if (elements.size() > 0) {
			BPUserLibraryElement element = (BPUserLibraryElement) elements
					.get(0);
			BPListElement[] children = element.getChildren();
			for (BPListElement child : children) {
				IPath path = child.getPath();
				root = EnvironmentPathUtils.getLocalPath(path).toFile();
				break;
			}
		}
		LibraryDeploymentUtils handler = new LibraryDeploymentUtils();
		handler.openLibraryDeploymentWizard(root);
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
		if (selected.size() == 1) {
			Object element = selected.get(0);
			if (element instanceof BPUserLibraryElement) {
				BPListElement[] children = ((BPUserLibraryElement) element)
						.getChildren();
				if (children.length > 0) {
					return true;
				}
			}
		}
		return false;
	}

}
