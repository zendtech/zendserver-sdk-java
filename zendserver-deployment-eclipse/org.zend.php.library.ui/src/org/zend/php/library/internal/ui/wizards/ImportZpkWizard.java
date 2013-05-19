/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ImportZpkWizard extends Wizard {

	private ImportZpkPage importPage;

	private BPUserLibraryElement element;

	public ImportZpkWizard() {
		setWindowTitle(Messages.ImportZpkWizard_Title);
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		importPage = new ImportZpkPage();
		addPage(importPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		final ImportZpkData data = importPage.getData();
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				@SuppressWarnings("restriction")
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.ImportZpkWizard_TaskTitle,
							IProgressMonitor.UNKNOWN);
					if (alreadyExist(data.getName())) {
						importPage.statusChanged(new Status(
								IStatus.ERROR,
								LibraryUI.PLUGIN_ID,
								MessageFormat
										.format(Messages.ImportZpkWizard_AlreadyExistsError,
												data.getName())));
						return;
					}
					File root = LibraryUtils.unzipToSharedFolder(
							data.getName(), data.getVersion(),
							new File(data.getPath()));
					BPUserLibraryElement element = new BPUserLibraryElement(
							data.getName(), false, new BPListElement[0], data
									.getVersion());
					BPListElement curr = new BPListElement(null, null,
							IBuildpathEntry.BPE_LIBRARY, EnvironmentPathUtils
									.getFullPath(EnvironmentManager
											.getLocalEnvironment(), new Path(
											root.getAbsolutePath())), null,
							true);
					element.add(curr);
					ImportZpkWizard.this.element = element;
				}
			});
		} catch (InvocationTargetException e) {
			LibraryUI.log(e);
		} catch (InterruptedException e) {
			LibraryUI.log(e);
		}
		return element != null;
	}

	public BPUserLibraryElement getElement() {
		return element;
	}

	private boolean alreadyExist(String name) {
		String[] names = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		for (String n : names) {
			if (n.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
