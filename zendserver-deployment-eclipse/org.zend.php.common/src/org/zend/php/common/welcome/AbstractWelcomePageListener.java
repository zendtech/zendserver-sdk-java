/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.common.welcome;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.zend.php.common.Activator;


public abstract class AbstractWelcomePageListener implements Runnable {

	public abstract IWizard getWizard(IWorkbench workbench);

	public void run() {
		IWorkbench w = Activator.getDefault().getWorkbench();
		IWizard wizard = getWizard(w);
		WizardDialog dialog = new WizardDialog(w.getActiveWorkbenchWindow()
				.getShell(), wizard);
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
	protected static WizardDialog createDialog(Shell parent, IWizard wizard) {
		WizardDialog dialog = new WizardDialog(parent, wizard);
		dialog.setHelpAvailable(false);
		dialog.create();
		dialog.getShell().setSize(550, 500);

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = parent.getBounds();
		Point dialogSize = dialog.getShell().getSize();

		dialog.getShell().setLocation(
				shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
				shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

		return dialog;
	}

}