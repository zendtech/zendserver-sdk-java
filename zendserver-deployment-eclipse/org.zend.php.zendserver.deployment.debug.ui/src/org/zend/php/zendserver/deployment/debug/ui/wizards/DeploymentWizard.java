/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

public class DeploymentWizard extends Wizard {

	public enum Mode {
		RUN, DEBUG, DEPLOY;
	}

	protected ConfigurationPage configPage;
	protected ParametersPage parametersPage;
	protected IDescriptorContainer model;
	protected IProject project;
	protected IDeploymentHelper helper;
	protected String help;
	protected String description;

	public DeploymentWizard(ILaunchConfiguration config, Mode mode) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		DeploymentHelper helper = DeploymentHelper.create(config);
		String projectName = helper.getProjectName();
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		init(project, helper, mode);
	}

	public DeploymentWizard(IProject project, IDeploymentHelper helper,
			Mode mode) {
		setDialogSettings(Activator.getDefault().getDialogSettings());
		init(project, helper, mode);
	}

	@Override
	public void addPages() {
		super.addPages();
		this.configPage = new ConfigurationPage(helper, getContainer(),
				getWindowTitle(), description, help);
		addPage(configPage);
		List<IParameter> parameters = model.getDescriptorModel()
				.getParameters();
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

	protected void init(IProject project, IDeploymentHelper helper, Mode mode) {
		IResource descriptor = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.project = project;
		this.model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		setNeedsProgressMonitor(true);
		String title = null;
		String image = null;
		switch (mode) {
		case RUN:
			title = Messages.deploymentWizard_LaunchTitle;
			description = Messages.DeploymentWizard_LaunchDesc;
			image = Activator.IMAGE_WIZBAN_DEP;
			help = HelpContextIds.LAUNCHING_AN_APPLICATION;
			break;
		case DEBUG:
			title = Messages.deploymentWizard_DebugTitle;
			description = Messages.DeploymentWizard_DebugDesc;
			image = Activator.IMAGE_WIZBAN_DEBUG;
			help = HelpContextIds.DEBUGGING_AN_APPLICAITON;
			break;
		case DEPLOY:
			title = Messages.deploymentWizard_DeployTitle;
			description = Messages.DeploymentWizard_DeployDesc;
			image = Activator.IMAGE_WIZBAN_DEPLOY;
			help = HelpContextIds.DEPLOYING_AN_APPLICATION;
			break;
		}
		this.parametersPage = new ParametersPage(project, helper, title, help);
		if (helper == null || helper.getProjectName().isEmpty()) {
			this.helper = updateHelper(LaunchUtils.createDefaultHelper(project));
		} else {
			this.helper = updateHelper(helper);
		}
		setWindowTitle(title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(image));
	}

	protected IDeploymentHelper updateHelper(IDeploymentHelper toUpdate) {
		// Set default dialog settings
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String targetId = settings.get(DeploymentAttributes.TARGET_ID
					.getName());
			if (targetId != null && toUpdate.getTargetId() == null) {
				IZendTarget target = TargetsManagerService.INSTANCE
						.getTargetManager().getTargetById(targetId);
				if (target != null) {
					toUpdate.setTargetId(targetId);
					toUpdate.setTargetHost(target.getHost().getHost());
					URL targetUrl = target.getDefaultServerURL();
					try {
						URL baseUrl = new URL(targetUrl.getProtocol(),
								targetUrl.getHost(), targetUrl.getPort(), "/"
										+ toUpdate.getAppName());
						toUpdate.setBaseURL(baseUrl.toString());
					} catch (MalformedURLException e) {
						Activator.log(e);
					}
				}
			}
			String developerMode = settings
					.get(DeploymentAttributes.DEVELOPMENT_MODE.getName());
			if (developerMode != null) {
				toUpdate.setDevelopmentMode((Boolean.valueOf(developerMode)));
			}
			String warnUpdate = settings.get(DeploymentAttributes.WARN_UPDATE
					.getName());
			if (warnUpdate != null) {
				toUpdate.setWarnUpdate(Boolean.valueOf(warnUpdate));
			}
			String ignoreFailure = settings
					.get(DeploymentAttributes.IGNORE_FAILURES.getName());
			if (ignoreFailure != null) {
				toUpdate.setIgnoreFailures(Boolean.valueOf(ignoreFailure));
			}
		}
		String appName = toUpdate.getAppName();
		if (appName == null || appName.isEmpty()) {
			toUpdate.setAppName(project.getName());
		}
		return toUpdate;
	}

	protected IDeploymentHelper createHelper() {
		IDeploymentHelper helper = configPage.getHelper();
		helper.setProjectName(project.getName());
		helper.setUserParams(parametersPage.getHelper().getUserParams());
		return helper;
	}

	protected void saveSettings(IDeploymentHelper helper) {
		IDialogSettings settings = getDialogSettings();
		settings.put(DeploymentAttributes.TARGET_ID.getName(),
				helper.getTargetId());
		settings.put(DeploymentAttributes.WARN_UPDATE.getName(),
				helper.isWarnUpdate());
		settings.put(DeploymentAttributes.IGNORE_FAILURES.getName(),
				helper.isIgnoreFailures());
		settings.put(DeploymentAttributes.DEVELOPMENT_MODE.getName(),
				helper.isDevelopmentModeEnabled());
	}

}
