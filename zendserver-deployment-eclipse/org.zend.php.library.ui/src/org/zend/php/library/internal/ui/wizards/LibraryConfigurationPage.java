/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationPage;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryConfigurationPage extends ConfigurationPage {

	protected LibraryConfigurationPage(IDeploymentHelper helper,
			IRunnableContext context, String title, String description,
			String help) {
		super(helper, context, title, description, help);
		this.block = new LibraryConfigurationBlock(this, description);
	}

	protected LibraryConfigurationPage(IDeploymentHelper helper,
			String description, IRunnableContext context) {
		this(helper, context, Messages.LibraryConfigurationPage_Title,
				description, null);
	}

	protected LibraryConfigurationPage() {
		this(null, null, null);
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
		if (helper != null) {
			block.setDialogSettings(getDialogSettings());
			block.initializeFields(helper);
		}
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

}
