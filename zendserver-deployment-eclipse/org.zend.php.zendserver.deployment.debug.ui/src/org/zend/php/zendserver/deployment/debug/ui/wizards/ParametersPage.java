/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class ParametersPage extends DeploymentWizardPage {

	private IProject project;
	private ParametersBlock block;

	public ParametersPage(IProject project, IDeploymentHelper helper,
			String title, String help) {
		super(Messages.parametersPage_Name, helper, help);
		setDescription(Messages.parametersPage_Description);
		setTitle(title);
		this.project = project;
		this.block = new ParametersBlock(this);
	}

	public ParametersPage(IProject project, IDeploymentHelper helper) {
		this(project, helper, Messages.parametersPage_Title, null);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite container = block.createContents(parent);
		block.createParametersGroup(project);
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
