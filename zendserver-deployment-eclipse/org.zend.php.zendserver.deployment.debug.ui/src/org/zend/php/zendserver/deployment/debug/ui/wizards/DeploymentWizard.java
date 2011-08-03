/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationBlock.OperationType;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class DeploymentWizard extends Wizard {

	private ConfigurationPage configPage;
	private ParametersPage parametersPage;
	private IDescriptorContainer model;
	private IProject project;
	private DeploymentHelper helper;
	private OperationType operationType;

	public DeploymentWizard(IProject project, IDeploymentHelper helper) {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.project = project;
		this.model = DescriptorContainerManager.getService().openDescriptorContainer(
				(IFile) descriptor);
		this.parametersPage = new ParametersPage(project, helper);
		if (helper == null || helper.getProjectName().isEmpty()) {
			helper = createDefaultHelper(project);
		}
		this.configPage = new ConfigurationPage(helper, this);
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.deploymentWizard_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
	}

	private IDeploymentHelper createDefaultHelper(IProject project) {
		IDeploymentHelper helper = new DeploymentHelper();
		helper.setAppName(project.getName());
		helper.setBasePath("/" + project.getName());
		helper.setDefaultServer(true);
		return helper;
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(configPage);
		List<IParameter> parameters = model.getDescriptorModel().getParameters();
		if (parameters != null && parameters.size() > 0) {
			addPage(parametersPage);
		}
	}

	@Override
	public boolean performFinish() {
		helper = createHelper();
		operationType = configPage.getOperationType();
		return true;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	public DeploymentHelper getHelper() {
		return helper;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	private DeploymentHelper createHelper() {
		DeploymentHelper helper = new DeploymentHelper();
		URL url = configPage.getBaseUrl();
		helper.setBasePath(url.getPath());
		helper.setProjectName(project.getName());
		helper.setTargetId(configPage.getTarget().getId());
		if (configPage.getOperationType() == OperationType.UPDATE) {
			ApplicationInfo info = configPage.getApplicationToUpdate();
			if (info != null) {
				helper.setAppId(info.getId());
			}
		}
		helper.setUserParams(parametersPage.getParameters());
		helper.setAppName(configPage.getUserAppName());
		helper.setIgnoreFailures(configPage.isIgnoreFailures());
		helper.setDefaultServer(configPage.isDefaultServer());
		helper.setVirtualHost(url.getHost());
		return helper;
	}

}
