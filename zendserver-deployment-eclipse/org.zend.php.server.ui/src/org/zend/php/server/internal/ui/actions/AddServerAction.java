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

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.ui.ServerWizard;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.ServersUI;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class AddServerAction extends AbstractServerAction {

	public AddServerAction() {
		super(Messages.AddServerAction_AddLabel, ServersUI
				.getImageDescriptor(ServersUI.ADD_ICON));
	}

	@Override
	public void run() {
		Server toAdd = getServerFromWizard();
		if (toAdd != null) {
			ServersManager.addServer(toAdd);
			ServersManager.save();
		}
	}

	protected Server getServerFromWizard() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		Server server = null;
		ServerWizard wizard = new ServerWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() == Window.CANCEL) {
			return null;
		}
		server = (Server) wizard.getRootFragment().getWizardModel()
				.getObject(WizardModel.SERVER);
		return server;
	}

}
