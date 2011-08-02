/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;

public class ParametersPage extends DeploymentWizardPage {

	private IProject project;

	protected ParametersPage(IProject project, IDeploymentHelper helper) {
		super("Deployment Parameters Page", helper);
		setDescription("Set Deployment Parameters");
		setTitle("PHP Application Deployment");
		this.project = project;
		this.block = new ParametersBlock(this);
	}

	private ParametersBlock block;

	public void createControl(Composite parent) {
		Composite container = block.createContents(parent);
		block.createParametersGroup(project);
		setControl(container);
		if (getHelper() != null) {
			block.initializeFields(getHelper());
		}
		setPageComplete(false);
		statusChanged(block.validatePage());
	}

	public Map<String, String> getParameters() {
		return block.getParameters();
	}

}
