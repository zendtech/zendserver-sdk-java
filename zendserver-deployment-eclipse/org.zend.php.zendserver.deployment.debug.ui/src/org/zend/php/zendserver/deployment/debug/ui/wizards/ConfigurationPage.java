/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.URL;

import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.sdklib.target.IZendTarget;

public class ConfigurationPage extends DeploymentWizardPage {

	protected ConfigurationPage(IDeploymentHelper helper) {
		super("Deployment Page", helper);
		setDescription("Overall Deployment Settings");
		setTitle("PHP Application Deployment");
		this.block = new ConfigurationBlock(this);
	}

	protected ConfigurationPage() {
		this(null);
	}

	private ConfigurationBlock block;

	public void createControl(Composite parent) {
		Composite container = block.createContents(parent);
		setControl(container);
		if (getHelper() != null) {
			block.initializeFields(getHelper());
		}
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	public URL getBaseUrl() {
		return block.getBaseURL();
	}

	public String getUserAppName() {
		return block.getUserAppName();
	}

	public boolean isDefaultServer() {
		return block.isDefaultServer();
	}

	public boolean isIgnoreFailures() {
		return block.isIgnoreFailures();
	}

	public IZendTarget getTarget() {
		return block.getTarget();
	}

}
