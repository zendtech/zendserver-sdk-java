/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.zend.php.library.core.deploy.LibraryDeploymentAttributes;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeploymentWizard extends DeploymentWizard {

	public LibraryDeploymentWizard(IProject project, Mode mode) {
		super(project, null, mode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		this.configPage = new LibraryConfigurationPage(helper, getContainer(),
				getWindowTitle(), description, help);
		addPage(configPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * init(org.eclipse.core.resources.IProject,
	 * org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper,
	 * org.zend
	 * .php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode)
	 */
	protected void init(IProject project, IDeploymentHelper helper, Mode mode) {
		IResource descriptor = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.project = project;
		this.model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		setNeedsProgressMonitor(true);
		description = Messages.LibraryDeploymentWizard_Description;
		// TODO change help context
		help = null; // HelpContextIds.DEPLOYING_AN_APPLICATION;
		this.helper = createDefaultHelper();
		setWindowTitle(Messages.LibraryDeploymentWizard_Title);
		// TODO set image
		// image = Activator.IMAGE_WIZBAN_DEPLOY;
		// setDefaultPageImageDescriptor(LibraryUI.getImageDescriptor(image));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * createDefaultHelper()
	 */
	protected IDeploymentHelper createDefaultHelper() {
		IDeploymentHelper helper = super.createDefaultHelper();
		Map<String, String> extraAttributes = new HashMap<String, String>();
		extraAttributes.put(LibraryDeploymentAttributes.ADD_LIBRARY.getName(),
				String.valueOf(true));
		helper.setExtraAtttributes(extraAttributes);
		return helper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * createHelper()
	 */
	protected IDeploymentHelper createHelper() {
		IDeploymentHelper helper = configPage.getHelper();
		helper.setProjectName(project.getName());
		return helper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * saveSettings
	 * (org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper)
	 */
	protected void saveSettings(IDeploymentHelper helper) {
		IDialogSettings settings = getDialogSettings();
		settings.put(LibraryDeploymentAttributes.TARGET_ID.getName(),
				helper.getTargetId());
		settings.put(LibraryDeploymentAttributes.WARN_UPDATE.getName(),
				helper.isWarnUpdate());
		settings.put(LibraryDeploymentAttributes.ADD_LIBRARY.getName(),
				helper.isWarnUpdate());
	}

}
