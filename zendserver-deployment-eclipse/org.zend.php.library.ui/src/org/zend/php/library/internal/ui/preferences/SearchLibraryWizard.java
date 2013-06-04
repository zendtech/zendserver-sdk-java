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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPUserLibraryElement;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.composer.ComposerService;
import org.zend.php.library.internal.ui.LibraryUI;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class SearchLibraryWizard extends Wizard {

	private LibraryDetailsPage detailsPage;
	private SearchLibraryPage searchPage;

	private EclipseConsoleLog log;
	
	List<BPUserLibraryElement> elements;

	public SearchLibraryWizard(Map<String, LibraryVersion> libraries) {
		setWindowTitle(Messages.SearchLibraryWizard_0);
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(LibraryUI
				.getImageDescriptor("icons/full/wizban/packagist.png")); //$NON-NLS-1$
		searchPage = new SearchLibraryPage();
		detailsPage = new LibraryDetailsPage(libraries);
		this.log = new EclipseConsoleLog("Composer"); //$NON-NLS-1$
		this.log.init();
	}

	@Override
	public void addPages() {
		addPage(searchPage);
		addPage(detailsPage);
	}

	public List<BPUserLibraryElement> getElements() {
		return elements;
	}

	/*
	 * public IWizardPage getNextPage(IWizardPage page) { if (page ==
	 * searchPage) { RepositoryPackage selection = searchPage.getSelection(); if
	 * (selection != null) { detailsPage.initializeFields(selection); } } return
	 * super.getNextPage(page); }
	 */

	@Override
	public boolean performFinish() {
		// PHPexes execs = PHPexes.getInstance();
		// PHPexeItem[] allExecs = execs.getAllItems();
		// for (PHPexeItem exec : allExecs) {
		// if (exec.isDefault()) {
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Downloading selected library...", //$NON-NLS-1$
							IProgressMonitor.UNKNOWN);
					Map<IPath, List<String>> toAdd = ComposerService
							.downloadPackages(searchPage.getSelection(),
									detailsPage.getRequires(),
									detailsPage.getVersion(), log);
					Set<IPath> keys = toAdd.keySet();
					elements = new ArrayList<BPUserLibraryElement>();
					for (IPath key : keys) {
						List<String> paths = toAdd.get(key);
						BPUserLibraryElement element = new BPUserLibraryElement(
								key.segment(0), false, new BPListElement[0],
								key.segment(1));
						for (String path : paths) {
							BPListElement curr = new BPListElement(null, null,
									IBuildpathEntry.BPE_LIBRARY,
									EnvironmentPathUtils.getFullPath(
											EnvironmentManager
													.getLocalEnvironment(),
											new Path(path)), null, true);
							element.add(curr);
						}
						elements.add(element);
					}
				}
			});
			return true;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return false;
	}
}
