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
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class DeploymentWizard extends Wizard {

	public enum Mode {
		RUN, DEBUG, DEPLOY;
	}

	private ConfigurationPage configPage;
	private ParametersPage parametersPage;
	private IDescriptorContainer model;
	private IProject project;
	private IDeploymentHelper helper;
	private String help;

	public DeploymentWizard(ILaunchConfiguration config, Mode mode) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		DeploymentHelper helper = DeploymentHelper.create(config);
		String projectName = helper.getProjectName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		init(project, helper, mode);
	}
	
	public DeploymentWizard(IProject project, IDeploymentHelper helper,
			Mode mode) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		init(project, helper, mode);
	}

	private void init(IProject project, IDeploymentHelper helper, Mode mode) {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.project = project;
		this.model = DescriptorContainerManager.getService().openDescriptorContainer(
				(IFile) descriptor);
		setNeedsProgressMonitor(true);
		String title = null;
		String image = null;
		switch (mode) {
		case RUN:
			title = Messages.deploymentWizard_LaunchTitle;
			image = Activator.IMAGE_WIZBAN_DEP;
			help = HelpContextIds.LAUNCHING_AN_APPLICATION;
			break;
		case DEBUG:
			title = Messages.deploymentWizard_DebugTitle;
			image = Activator.IMAGE_WIZBAN_DEBUG;
			help = HelpContextIds.DEBUGGING_AN_APPLICAITON;
			break;
		case DEPLOY:
			title = Messages.deploymentWizard_DeployTitle;
			image = Activator.IMAGE_WIZBAN_DEPLOY;
			help = HelpContextIds.DEPLOYING_AN_APPLICATION;
			break;
		}
		this.parametersPage = new ParametersPage(project, helper, title, help);
		if (helper == null || helper.getProjectName().isEmpty()) {
			this.helper = createDefaultHelper();
		} else {
			this.helper = updateHelper(helper);
		}
		setWindowTitle(title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(image));
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
		this.configPage = new ConfigurationPage(helper, getContainer(),
				getWindowTitle(), help);
		addPage(configPage);
		List<IParameter> parameters = model.getDescriptorModel().getParameters();
		if (parameters != null && parameters.size() > 0) {
			addPage(parametersPage);
		}
	}

	@Override
	public boolean performFinish() {
		helper = createHelper();
		saveSettings(helper);
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

	private void saveSettings(IDeploymentHelper helper) {
		IDialogSettings settings = getDialogSettings();
		settings.put(DeploymentAttributes.TARGET_ID.getName(),
				helper.getTargetId());
	}

}
