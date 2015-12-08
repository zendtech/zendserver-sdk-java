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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ImportZpkPage extends WizardPage implements IStatusChangeListener {

	private ImportZpkBlock block;

	protected ImportZpkPage() {
		super("Configuration Page"); //$NON-NLS-1$
		setTitle(Messages.ImportZpkPage_Title);
		this.block = new ImportZpkBlock(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = block.createContents(parent, true);
		setControl(container);
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	public ImportZpkData getData() {
		return block.getData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener
	 * #statusChanged(org.eclipse.core.runtime.IStatus)
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
