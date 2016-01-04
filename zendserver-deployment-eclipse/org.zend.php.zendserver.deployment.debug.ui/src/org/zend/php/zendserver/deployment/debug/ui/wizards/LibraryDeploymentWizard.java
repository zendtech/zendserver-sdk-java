/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.UserLibraryManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeploymentAttributes;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
@SuppressWarnings("restriction")
public class LibraryDeploymentWizard extends AbstractLibraryWizard {

	private LibraryConfigurationPage configPage;

	public LibraryDeploymentWizard(IProject project, String targetId) {
		super();
		init(project, targetId);
	}

	public LibraryDeploymentWizard(LibraryDeployData data) {
		super();
		init(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		this.configPage = new LibraryConfigurationPage(getData());
		addPage(configPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		setData(configPage.getData());
		String[] names = DLTKCore.getUserLibraryNames(PHPLanguageToolkit.getDefault());
		for (String name : names) {
			if (name.equals(getData().getName())) {
				try {
					getContainer().run(true, false, new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask(Messages.BuildpathContainerWizard_InitJob, IProgressMonitor.UNKNOWN);
							try {
								DLTKCore.getBuildpathContainer(
										new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID).append(UserLibraryManager
												.makeLibraryName(getData().getName(), PHPLanguageToolkit.getDefault())),
										createPlaceholderProject());
							} catch (ModelException e) {
								Activator.log(e);
							}
						}
					});
				} catch (InvocationTargetException e) {
					Activator.log(e);
				} catch (InterruptedException e) {
					Activator.log(e);
				}
			}
		}
		saveSettings(getData());
		return true;
	}

	protected void init(IProject project, String targetId) {
		setData(createDefaultData(project, targetId));
		setWindowTitle(Messages.LibraryDeploymentWizard_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEPLOY_LIBRARY));
	}

	private void init(LibraryDeployData data) {
		setData(data);
		setWindowTitle(Messages.LibraryDeploymentWizard_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEPLOY_LIBRARY));
	}

	private LibraryDeployData createDefaultData(IProject project, String targetId) {
		IResource res = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService().openDescriptorContainer((IFile) res);
		IDeploymentDescriptor descriptor = model.getDescriptorModel();
		LibraryDeployData data = new LibraryDeployData();
		data.setName(descriptor.getName());
		data.setVersion(descriptor.getReleaseVersion());
		data.setRoot(project.getLocation().toFile());
		data.setProject(project);
		if (targetId == null) {
			setTarget(data, project);
		} else {
			data.setTargetId(targetId);
		}
		return data;
	}

	private void setTarget(LibraryDeployData data, IProject project) {
		IZendTarget target = DeploymentUtils.getTargetFromPreferences(project.getName());
		if (target != null) {
			data.setTargetId(target.getId());
		}
	}

	protected void saveSettings(LibraryDeployData data) {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			settings.put(LibraryDeploymentAttributes.TARGET_ID.getName(), data.getTargetId());
			if (data.isEnableAddLibrary()) {
				settings.put(LibraryDeploymentAttributes.ADD_LIBRARY.getName(), data.isAddPHPLibrary());
			}
			settings.put(LibraryDeploymentAttributes.SET_AS_DEFAULT.getName(), data.makeDefault());
		}
	}

	private static IScriptProject createPlaceholderProject() {
		String name = "####internal"; //$NON-NLS-1$
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		while (true) {
			IProject project = root.getProject(name);
			if (!project.exists()) {
				return DLTKCore.create(project);
			}
			name += '1';
		}
	}

}
