/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.w3c.dom.Document;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.core.deploy.LibraryDeploymentAttributes;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeploymentWizard extends Wizard {

	private LibraryDeployData data;
	private LibraryConfigurationPage configPage;

	public LibraryDeploymentWizard(IProject project) {
		setDialogSettings(LibraryUI.getDefault().getDialogSettings());
		init(project);
	}

	public LibraryDeploymentWizard(File root) {
		setDialogSettings(LibraryUI.getDefault().getDialogSettings());
		init(root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		this.configPage = new LibraryConfigurationPage(data);
		addPage(configPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		data = configPage.getData();
		saveSettings(data);
		return true;
	}

	public LibraryDeployData getData() {
		return data;
	}

	protected void init(File root) {
		this.data = createDefaultData(root);
		setWindowTitle(Messages.LibraryDeploymentWizard_Title);
		setNeedsProgressMonitor(true);
		// TODO set image
		// image = Activator.IMAGE_WIZBAN_DEPLOY;
		// setDefaultPageImageDescriptor(LibraryUI.getImageDescriptor(image));
	}

	protected void init(IProject project) {
		this.data = createDefaultData(project);
		setWindowTitle(Messages.LibraryDeploymentWizard_Title);
		setNeedsProgressMonitor(true);
		// TODO set image
		// image = Activator.IMAGE_WIZBAN_DEPLOY;
		// setDefaultPageImageDescriptor(LibraryUI.getImageDescriptor(image));
	}

	private LibraryDeployData createDefaultData(IProject project) {
		IResource res = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) res);
		IDeploymentDescriptor descriptor = model.getDescriptorModel();
		LibraryDeployData data = new LibraryDeployData();
		data.setName(descriptor.getName());
		data.setVersion(descriptor.getReleaseVersion());
		data.setRoot(project.getLocation().toFile());
		return data;
	}

	private LibraryDeployData createDefaultData(File root) {
		LibraryDeployData data = new LibraryDeployData();
		Document doc = LibraryUtils.getDeploymentDescriptor(root);
		if (doc != null) {
			data.setName(LibraryUtils.getLibraryName(doc));
			data.setVersion(LibraryUtils.getLibraryVersion(doc));
		}
		data.setRoot(root);
		data.setEnableAddLibrary(false);
		return data;
	}

	protected void saveSettings(LibraryDeployData data) {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			settings.put(LibraryDeploymentAttributes.TARGET_ID.getName(),
					data.getTargetId());
			settings.put(LibraryDeploymentAttributes.WARN_UPDATE.getName(),
					data.isWarnSynchronize());
			settings.put(LibraryDeploymentAttributes.ADD_LIBRARY.getName(),
					data.isAddPHPLibrary());
		}
	}

}
