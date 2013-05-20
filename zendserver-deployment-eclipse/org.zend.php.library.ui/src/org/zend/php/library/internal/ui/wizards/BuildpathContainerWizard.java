/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.ui.wizards.AbstractUserLibraryWizard;
import org.zend.php.library.core.LibraryManager;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPLibraryDependency;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class BuildpathContainerWizard extends AbstractUserLibraryWizard {

	private IBuildpathEntry fEntryToEdit;
	private IBuildpathEntry[] fNewEntries;
	private UserLibraryWizardPage fUserLibraryPage;
	private IScriptProject fCurrProject;
	private IBuildpathEntry[] fCurrBuildpath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.wizards.AbstractUserLibraryWizard#init(org.eclipse
	 * .dltk.core.IScriptProject, org.eclipse.dltk.core.IBuildpathEntry[])
	 */
	public void init(IScriptProject currProject, IBuildpathEntry[] currEntries) {
		init(null, currProject, currEntries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.wizards.AbstractUserLibraryWizard#init(org.eclipse
	 * .dltk.core.IBuildpathEntry, org.eclipse.dltk.core.IScriptProject,
	 * org.eclipse.dltk.core.IBuildpathEntry[])
	 */
	public void init(IBuildpathEntry entryToEdit, IScriptProject currProject,
			IBuildpathEntry[] currEntries) {
		fEntryToEdit = entryToEdit;
		fNewEntries = null;

		fCurrProject = currProject;
		fCurrBuildpath = currEntries;

		String title;
		if (entryToEdit == null) {
			title = NewWizardMessages.BuildpathContainerWizard_new_title;
		} else {
			title = NewWizardMessages.BuildpathContainerWizard_edit_title;
		}
		setWindowTitle(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWizard#performFinish()
	 */
	public boolean performFinish() {
		if (fUserLibraryPage != null) {
			if (fEntryToEdit == null) {
				fNewEntries = fUserLibraryPage.getNewContainers();
			} else {
				IBuildpathEntry entry = fUserLibraryPage.getSelection();
				fNewEntries = (entry != null) ? new IBuildpathEntry[] { entry }
						: null;
			}
			addDependences();
			addSources();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWizard#addPages()
	 */
	public void addPages() {
		fUserLibraryPage = new UserLibraryWizardPage();
		fUserLibraryPage.initialize(fCurrProject, fCurrBuildpath);
		fUserLibraryPage.setSelection(fEntryToEdit);
		addPage(fUserLibraryPage);
		super.addPages();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IWizard#canFinish()
	 */
	public boolean canFinish() {
		if (fUserLibraryPage != null) {
			return fUserLibraryPage.isPageComplete();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.ui.wizards.AbstractUserLibraryWizard#getNewEntries()
	 */
	protected IBuildpathEntry[] getNewEntries() {
		return fNewEntries;
	}

	private void addSources() {
		IPath sourcePath = fUserLibraryPage.getAddSource();
		if (sourcePath != null) {
			File destFile = fCurrProject.getProject().findMember(sourcePath)
					.getLocation().toFile();
			for (IBuildpathEntry enty : fNewEntries) {
				IPath path = enty.getPath();
				try {
					IBuildpathContainer container = DLTKCore
							.getBuildpathContainer(path, fCurrProject);
					IBuildpathEntry[] entries = container.getBuildpathEntries();
					for (IBuildpathEntry entry : entries) {
						LibraryManager.addLibraryToProject(
								destFile,
								EnvironmentPathUtils.getLocalPath(
										entry.getPath()).toFile(),
								path.segment(1));
					}
				} catch (ModelException e) {
					LibraryUI.log(e);
				}
			}
			try {
				fCurrProject.getProject().refreshLocal(IResource.DEPTH_INFINITE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addDependences() {
		if (fUserLibraryPage.getAddDependency()) {
			final IDescriptorContainer container = DescriptorContainerManager
					.getService().openDescriptorContainer(
							fCurrProject.getProject());
			IDeploymentDescriptor model = container.getDescriptorModel();
			List<IPHPLibraryDependency> dependences = model
					.getPHPLibraryDependencies();
			if (fEntryToEdit != null) {
				IPHPLibraryDependency entryToRemove = null;
				String nameToRemove = fEntryToEdit.getPath().segment(1);
				for (IPHPLibraryDependency d : dependences) {
					if (nameToRemove.equals(d.getName())) {
						entryToRemove = d;
						break;
					}
				}
				if (entryToRemove != null) {
					dependences.remove(entryToRemove);
				}
			}
			for (IBuildpathEntry entry : fNewEntries) {
				String name = entry.getPath().segment(1);
				for (IPHPLibraryDependency d : dependences) {
					if (name.equals(d.getName())) {
						name = null;
						break;
					}
				}
				if (name != null) {
					IPHPLibraryDependency dependency = (IPHPLibraryDependency) DeploymentDescriptorFactory
							.createModelElement(DeploymentDescriptorPackage.DEPENDENCIES_LIBRARY);

					dependency.setName(name);
					String version = DLTKCore.getUserLibraryVersion(name,
							PHPLanguageToolkit.getDefault());
					if (version != null) {
						dependency.setEquals(version);
					}
					dependences.add(dependency);
				}
			}
			container.save();
		}
	}

}
