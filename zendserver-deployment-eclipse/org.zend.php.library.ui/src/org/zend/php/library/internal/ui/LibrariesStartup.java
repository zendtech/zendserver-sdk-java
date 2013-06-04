/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.ui.IStartup;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.ui.IBuiltInLibrary;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LibrariesStartup implements IStartup {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		List<IBuiltInLibrary> libraries = getLibraries();
		List<String> existingNames = Arrays.asList(DLTKCore
				.getUserLibraryNames(PHPLanguageToolkit.getDefault()));
		for (IBuiltInLibrary lib : libraries) {
			String name = lib.getName();
			if (existingNames.contains(name)) {
				continue;
			}
			String version = lib.getVersion();
			IBuildpathEntry[] entries = lib.getBuildpathEntries();
			ModelManager.getUserLibraryManager().setUserLibrary(
					LibraryUtils.createLibraryName(name), version, true,
					entries, false, PHPLanguageToolkit.getDefault());
		}
	}

	private List<IBuiltInLibrary> getLibraries() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(LibraryUI.PLUGIN_ID,
						"builtInLibrary"); //$NON-NLS-1$
		List<IBuiltInLibrary> result = new ArrayList<IBuiltInLibrary>();
		for (IConfigurationElement element : elements) {
			if ("library".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object library = element.createExecutableExtension("class"); //$NON-NLS-1$
					if (library instanceof IBuiltInLibrary) {
						result.add((IBuiltInLibrary) library);
					}
				} catch (CoreException e) {
					LibraryUI.log(e);
				}
			}
		}
		return result;
	}

}
