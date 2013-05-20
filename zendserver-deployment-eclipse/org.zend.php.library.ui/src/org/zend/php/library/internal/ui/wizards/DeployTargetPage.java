/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class DeployTargetPage extends WizardPage implements
		IStatusChangeListener {

	private DeployTargetBlock block;

	/**
	 * Create the wizard.
	 * 
	 * @param data
	 */
	public DeployTargetPage(LibraryDeployData data) {
		super(Messages.DeployTargetPage_TItle);
		setTitle(Messages.DeployTargetPage_TItle);
		setDescription(Messages.DeployTargetPage_2);
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
		// TODO set help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, null);
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	public LibraryDeployData getData() {
		return block.getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener
	 * #statusChanged(org.eclipse.core.runtime.IStatus)
	 */
	public void statusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			setMessage(status.getMessage());
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			if (status.getSeverity() == IStatus.ERROR) {
				setErrorMessage(status.getMessage());
			} else {
				setErrorMessage(null);
			}
			setPageComplete(false);
		}
	}

}
