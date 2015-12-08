/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.debug.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class DeployTargetPage extends WizardPage implements IStatusChangeListener {

	private DeployTargetBlock block;

	/**
	 * Create the wizard.
	 * 
	 * @param data
	 */
	public DeployTargetPage(LibraryDeployData data) {
		super(Messages.DeployTargetPage_TItle);
		setTitle(Messages.DeployTargetPage_TItle);
		this.block = new DeployTargetBlock(this, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationPage
	 * #createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = block.createContents(parent, true);
		setControl(container);
		parent.setData(WorkbenchHelpSystem.HELP_KEY, HelpContextIds.DEPLOY_PHP_LIBRARY_ON_TARGET);
		parent.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent arg0) {
				org.eclipse.swt.program.Program.launch(HelpContextIds.DEPLOY_PHP_LIBRARY_ON_TARGET);
			}
		});
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	public LibraryDeployData getData() {
		return block.getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.listeners.
	 * IStatusChangeListener #statusChanged(org.eclipse.core.runtime.IStatus)
	 */
	public void statusChanged(final IStatus status) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				switch (status.getSeverity()) {
				case IStatus.OK:
					setMessage(status.getMessage());
					setErrorMessage(null);
					setPageComplete(true);
					break;
				case IStatus.INFO:
					setErrorMessage(null);
					setMessage(status.getMessage(), IMessageProvider.INFORMATION);
					setPageComplete(false);
					break;
				case IStatus.WARNING:
					setErrorMessage(null);
					setMessage(status.getMessage(), IMessageProvider.WARNING);
					setPageComplete(false);
					break;
				case IStatus.ERROR:
					setErrorMessage(status.getMessage());
					setPageComplete(false);
					break;
				default:
					setErrorMessage(null);
					setPageComplete(false);
					break;
				}
			}
		});
	}
}
