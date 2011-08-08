/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;

public class ConfigurationPage extends DeploymentWizardPage {

	protected ConfigurationPage(IDeploymentHelper helper, IWizard wizard) {
		super("Deployment Page", helper);
		setDescription("Overall Deployment Settings");
		setTitle("PHP Application Deployment");
		this.block = new ConfigurationBlock(this, wizard);
	}

	protected ConfigurationPage() {
		this(null, null);
	}

	private ConfigurationBlock block;

	public void createControl(Composite parent) {
		Composite container = block.createContents(parent);
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
