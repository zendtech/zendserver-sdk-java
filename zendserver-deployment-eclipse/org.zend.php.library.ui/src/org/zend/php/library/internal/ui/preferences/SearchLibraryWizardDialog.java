/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.ui.preferences;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.library.internal.core.PackagistService;
import org.zend.php.library.internal.core.RepositoryPackage;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class SearchLibraryWizardDialog extends WizardDialog {

	public SearchLibraryWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardDialog#nextPressed()
	 */
	protected void nextPressed() {
		final IWizardPage page = getWizard().getStartingPage();
		if (page instanceof SearchLibraryPage) {
			final SearchLibraryPage searchPage = (SearchLibraryPage) page;
			final RepositoryPackage repositoryPackage = searchPage
					.getSelection();
			try {
				run(true, false, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask("Retireving library details...", //$NON-NLS-1$
								IProgressMonitor.UNKNOWN);
						RepositoryPackage pkg = PackagistService
								.getPackageInfo(repositoryPackage.getName());
						IWizardPage page = getWizard().getNextPage(searchPage);
						if (page instanceof LibraryDetailsPage) {
							LibraryDetailsPage libPage = (LibraryDetailsPage) page;
							libPage.initializeFields(pkg);
							libPage.setPageComplete(true);
						}
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				// TODO handle it
			} catch (InterruptedException e) {
				// TODO handle it
			}
		}
		super.nextPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardDialog#backPressed()
	 */
	protected void backPressed() {
		IWizardPage page = getWizard().getStartingPage();
		if (page instanceof SearchLibraryPage) {
			final SearchLibraryPage searchPage = (SearchLibraryPage) page;
			page = getWizard().getNextPage(searchPage);
			if (page instanceof LibraryDetailsPage) {
				LibraryDetailsPage libPage = (LibraryDetailsPage) page;
				libPage.initializeFields(null);
				libPage.setPageComplete(false);
			}
		}
		super.backPressed();
	}

}
