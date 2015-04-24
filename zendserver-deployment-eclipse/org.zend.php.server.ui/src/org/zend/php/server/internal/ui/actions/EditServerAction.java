/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.internal.ui.actions;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.ui.ServerEditWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.ServersUI;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class EditServerAction extends AbstractServerAction implements
		ISelectionChangedListener {

	private boolean isEnabled;

	public EditServerAction(ISelectionProvider provider) {
		super(Messages.EditServerAction_EditLabel, ServersUI
				.getImageDescriptor(ServersUI.EDIT_ICON), provider);
		provider.addSelectionChangedListener(this);
	}

	@Override
	public void run() {
		List<Server> toEdit = getSelection();
		if (!toEdit.isEmpty()) {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			ServerEditWizard wizard = new ServerEditWizard(
					(Server) toEdit.get(0));
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			// ServerEditDialog dialog = new ServerEditDialog(window.getShell(),
			// (Server) toEdit.get(0));
			if (dialog.open() == Window.CANCEL) {
				return;
			}
			ServersManager.save();
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		boolean newVal = !selection.isEmpty();
		if (isEnabled != newVal) {
			isEnabled = newVal;
			firePropertyChange(ENABLED, !isEnabled, isEnabled);
		}
	}

}
