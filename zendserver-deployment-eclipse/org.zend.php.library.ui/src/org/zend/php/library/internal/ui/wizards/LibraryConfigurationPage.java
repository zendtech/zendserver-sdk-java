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
public class LibraryConfigurationPage extends WizardPage implements
		IStatusChangeListener {

	private LibraryConfigurationBlock block;

	protected LibraryConfigurationPage(LibraryDeployData data) {
		super("Configuration Page"); //$NON-NLS-1$
		setTitle(Messages.LibraryConfigurationPage_Title);
		this.block = new LibraryConfigurationBlock(this, data);
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
		block.setDialogSettings(getDialogSettings());
		block.initializeFields();
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
