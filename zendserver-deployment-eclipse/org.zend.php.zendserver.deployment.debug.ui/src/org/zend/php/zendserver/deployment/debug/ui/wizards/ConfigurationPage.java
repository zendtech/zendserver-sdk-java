/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class ConfigurationPage extends DeploymentWizardPage {

	protected ConfigurationPage(IDeploymentHelper helper, IRunnableContext context) {
		super(Messages.configurationPage_Name, helper);
		setDescription(Messages.configurationPage_Description);
		setTitle(Messages.configurationPage_Title);
		this.block = new ConfigurationBlock(this, context);
	}

	protected ConfigurationPage() {
		this(null, null);
	}

	private ConfigurationBlock block;

	public void createControl(Composite parent) {
		Composite container = block.createContents(parent, true);
		setControl(container);
		if (helper != null) {
			block.initializeFields(helper);
		}
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	@Override
	public IDeploymentHelper getHelper() {
		return block.getHelper();
	}

}
