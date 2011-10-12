/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class DeploymentWizard extends Wizard {

	private ConfigurationPage configPage;
	private ParametersPage parametersPage;
	private IDescriptorContainer model;
	private IProject project;
	private IDeploymentHelper helper;

	public DeploymentWizard(ILaunchConfiguration config, boolean isLaunch) {
		DeploymentHelper helper = DeploymentHelper.create(config);
		String projectName = helper.getProjectName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		init(project, helper, isLaunch);
	}
	
	public DeploymentWizard(IProject project, IDeploymentHelper helper, boolean isLaunch) {
		init(project, helper, isLaunch);
	}

	private void init(IProject project, IDeploymentHelper helper, boolean isLaunch) {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.project = project;
		this.model = DescriptorContainerManager.getService().openDescriptorContainer(
				(IFile) descriptor);
		this.parametersPage = new ParametersPage(project, helper);
		if (helper == null || helper.getProjectName().isEmpty()) {
			this.helper = createDefaultHelper();
		} else {
			this.helper = updateHelper(helper);
		}
		setNeedsProgressMonitor(true);
		if (isLaunch) {
			setWindowTitle(Messages.deploymentWizard_LaunchTitle);
			setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		} else {
			setWindowTitle(Messages.deploymentWizard_DeployTitle);
			setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		}
	}

	private IDeploymentHelper updateHelper(IDeploymentHelper toUpdate) {
		if (toUpdate.getBaseURL() == null) {
			toUpdate.setBaseURL("http://default/" + project.getName()); //$NON-NLS-1$
			toUpdate.setDefaultServer(true);
		}
		String appName = toUpdate.getAppName();
		if (appName == null || appName.isEmpty()) {
			toUpdate.setAppName(project.getName());
		}
		return toUpdate;
	}

	private IDeploymentHelper createDefaultHelper() {
		IDeploymentHelper helper = new DeploymentHelper();
		helper.setBaseURL("http://default/" + project.getName()); //$NON-NLS-1$
		helper.setDefaultServer(true);
		helper.setAppName(project.getName());
		return helper;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setMinimumSize(550, 350);
		getShell().setSize(getShell().getMinimumSize());
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height) / 3;
		getShell().setLocation(x, y);
	}

	@Override
	public void addPages() {
		super.addPages();
		this.configPage = new ConfigurationPage(helper, getContainer());
		addPage(configPage);
		List<IParameter> parameters = model.getDescriptorModel().getParameters();
		if (parameters != null && parameters.size() > 0) {
			addPage(parametersPage);
		}
	}

	@Override
	public boolean performFinish() {
		helper = createHelper();
		return true;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	public IDeploymentHelper getHelper() {
		return helper;
	}

	private IDeploymentHelper createHelper() {
		IDeploymentHelper helper = configPage.getHelper();
		helper.setProjectName(project.getName());
		helper.setUserParams(parametersPage.getHelper().getUserParams());
		return helper;
	}

}
