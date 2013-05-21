/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.zend.php.library.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.UserLibrary;
import org.eclipse.dltk.internal.core.UserLibraryBuildpathContainer;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.zend.php.library.core.CustomBuildpathContainerInitializer;

public class UserLibraryBuildpathContainerInitializer extends
		BuildpathContainerInitializer {

	private IDLTKLanguageToolkit toolkit = PHPLanguageToolkit.getDefault();

	private static List<CustomBuildpathContainerInitializer> customInitializers;

	static {
		customInitializers = getCustomInitializers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.core.ClasspathContainerInitializer#
	 * canUpdateClasspathContainer(org.eclipse.core.runtime.IPath,
	 * org.eclipse.dltk.core.IJavaProject)
	 */
	public boolean canUpdateBuildpathContainer(IPath containerPath,
			IScriptProject project) {
		return isUserLibraryContainer(containerPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.core.ClasspathContainerInitializer#getComparisonID(org
	 * .eclipse.core.runtime.IPath, org.eclipse.dltk.core.IJavaProject)
	 */
	public Object getComparisonID(IPath containerPath, IScriptProject project) {
		return containerPath;
	}

	/**
	 * @see org.eclipse.dltk.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.dltk.core.IJavaProject)
	 */
	public String getDescription(IPath containerPath, IScriptProject project) {
		if (isUserLibraryContainer(containerPath)) {
			return containerPath.segment(1);
		}
		return super.getDescription(containerPath, project);
	}

	public void initialize(IPath containerPath, IScriptProject project)
			throws CoreException {
		for (CustomBuildpathContainerInitializer initialzier : customInitializers) {
			if (initialzier.getPath().equals(containerPath)) {
				initialzier.initialize(containerPath, project);
			}
		}
		if (isUserLibraryContainer(containerPath)) {
			String userLibName = containerPath.segment(1);
			IDLTKLanguageToolkit tk;
			tk = DLTKLanguageManager.getLanguageToolkit(project);
			int pos = userLibName.indexOf("#"); //$NON-NLS-1$
			if (pos != -1) {
				String nature = userLibName.substring(0, pos);
				userLibName = userLibName.substring(pos + 1);
				if (tk == null) {
					tk = DLTKLanguageManager.getLanguageToolkit(nature);
				}
			}
			if (tk == null) {
				tk = toolkit;
			}
			if (tk == null) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								DLTKCore.PLUGIN_ID,
								"DLTK Langauge toolkit is null"));
			}
			UserLibrary userLibrary = ModelManager.getUserLibraryManager()
					.getUserLibrary(userLibName, tk);
			if (userLibrary != null) {
				UserLibraryBuildpathContainer container = new UserLibraryBuildpathContainer(
						userLibName, tk);
				DLTKCore.setBuildpathContainer(containerPath,
						new IScriptProject[] { project },
						new IBuildpathContainer[] { container }, null);
			} else if (ModelManager.BP_RESOLVE_VERBOSE) {
				verbose_no_user_library_found(project, userLibName);
			}
		} else if (ModelManager.BP_RESOLVE_VERBOSE) {
			verbose_not_a_user_library(project, containerPath);
		}
	}

	private boolean isUserLibraryContainer(IPath path) {
		return path != null
				&& (path.segmentCount() == 2 || path.segmentCount() == 3)
				&& DLTKCore.USER_LIBRARY_CONTAINER_ID.equals(path.segment(0));
	}

	/**
	 * @see org.eclipse.dltk.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.dltk.core.IJavaProject,
	 *      org.eclipse.dltk.core.IClasspathContainer)
	 */
	public void requestBuildpathContainerUpdate(IPath containerPath,
			IScriptProject project, IBuildpathContainer containerSuggestion)
			throws CoreException {
		if (isUserLibraryContainer(containerPath)) {
			String name = containerPath.segment(1);
			String version = containerPath.segment(2);
			if (containerSuggestion != null) {
				ModelManager
						.getUserLibraryManager()
						.setUserLibrary(
								name,
								version,
								containerSuggestion.getBuildpathEntries(),
								containerSuggestion.getKind() == IBuildpathContainer.K_SYSTEM,
								toolkit);
			} else {
				ModelManager.getUserLibraryManager().removeUserLibrary(name,
						toolkit);
			}
			// update of affected projects was done as a consequence of
			// setUserLibrary() or removeUserLibrary()
		}
	}

	private void verbose_no_user_library_found(IScriptProject project,
			String userLibraryName) {
		Util.verbose("UserLibrary INIT - FAILED (no user library found)\n" + //$NON-NLS-1$
				"	project: " + project.getElementName() + '\n' + //$NON-NLS-1$
				"	userLibraryName: " + userLibraryName); //$NON-NLS-1$
	}

	private void verbose_not_a_user_library(IScriptProject project,
			IPath containerPath) {
		Util.verbose("UserLibrary INIT - FAILED (not a user library)\n" + //$NON-NLS-1$
				"	project: " + project.getElementName() + '\n' + //$NON-NLS-1$
				"	container path: " + containerPath); //$NON-NLS-1$
	}

	public void setToolkit(IDLTKLanguageToolkit languageToolkit) {
		this.toolkit = languageToolkit;
	}

	private static List<CustomBuildpathContainerInitializer> getCustomInitializers() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(LibraryCore.PLUGIN_ID,
						"userLibraryInitializer");
		List<CustomBuildpathContainerInitializer> result = new ArrayList<CustomBuildpathContainerInitializer>();
		for (IConfigurationElement element : elements) {
			if ("initializer".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object initializer = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (initializer instanceof CustomBuildpathContainerInitializer) {
						result.add((CustomBuildpathContainerInitializer) initializer);
					}
				} catch (CoreException e) {
					LibraryCore.log(e);
				}
			}
		}
		return result;
	}

}
