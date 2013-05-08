/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.library.core.LibraryVersion;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class SearchButtonHandler implements IPHPLibraryButtonHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getLabel()
	 */
	public String getLabel() {
		return "Search...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#handleSelection
	 * (java.util.List)
	 */
	public void handleSelection(TreeListDialogField field) {
		String[] names = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		Map<String, LibraryVersion> libs = new HashMap<String, LibraryVersion>();
		for (String name : names) {
			String version = DLTKCore.getUserLibraryVersion(name,
					PHPLanguageToolkit.getDefault());
			libs.put(name, LibraryVersion.byName(version));
		}
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		SearchLibraryWizard wizard = new SearchLibraryWizard(libs);
		SearchLibraryWizardDialog dialog = new SearchLibraryWizardDialog(shell,
				wizard);
		if (dialog.open() == Window.OK) {
			field.addElements(wizard.getElements());
			field.refresh();
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

	/* (non-Javadoc)
	 * @see org.eclipse.php.ui.preferences.IPHPLibraryButtonHandler#getPosition()
	 */
	public int getPosition() {
		return 1;
	}

}
