/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.utils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.osgi.framework.Bundle;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;

/**
 * Utility class for managing PHP Libraries.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
@SuppressWarnings("restriction")
public class LibraryManager {

	private static final String ZPK_EXTENSION = "zpk"; //$NON-NLS-1$
	private static final String SHARED_FOLDER_NAME = "libraries"; //$NON-NLS-1$

	public static final File SHARED_FOLDER;

	static {
		Bundle bundle = Platform.getBundle(DeploymentCore.PLUGIN_ID);
		SHARED_FOLDER = Platform.getStateLocation(bundle).append(SHARED_FOLDER_NAME).toFile();
		if (!SHARED_FOLDER.exists()) {
			SHARED_FOLDER.mkdirs();
		}
	}

	public static void addLibrary(LibraryDeployData data) throws IOException {
		addLibrary(data.getName(), data.getVersion(), data.getRoot());
	}

	public static void addLibrary(String name, String version, File root) throws IOException {
		File libraryRoot = getLibraryRoot(name, version);
		if (libraryRoot.exists())
			FileUtils.deleteDirectory(libraryRoot);

		if (root.isFile() && FilenameUtils.isExtension(root.getName(), ZPK_EXTENSION))
			LibraryUtils.unzipPackage(root, libraryRoot);
		else if (root.isDirectory())
			FileUtils.copyDirectory(root, libraryRoot);
		else {
			String message = MessageFormat.format(Messages.LibraryManager_UnknownLibraryFormat_Error, root.getAbsolutePath());
			throw new IOException(message);
		}

		addPHPLibrary(name, version, libraryRoot);
	}

	public static boolean hasLibrary(LibraryDeployData data) {
		return hasLibrary(data.getName(), data.getVersion());
	}

	public static boolean hasLibrary(String name, String version) {
		String[] libNames = DLTKCore.getUserLibraryNames(PHPLanguageToolkit.getDefault());
		for (String libName : libNames) {
			if (!libName.equalsIgnoreCase(name))
				continue;

			String libVersion = DLTKLibraryUtils.getUserLibraryVersion(name, PHPLanguageToolkit.getDefault());
			if (libVersion == null)
				continue;

			if (libVersion.equalsIgnoreCase(version))
				return true;
		}
		return false;
	}

	public static File getLibraryRoot(String name, String version) {
		File libFolder = new File(SHARED_FOLDER, name);
		File versionFolder = new File(libFolder, version);
		return versionFolder;
	}

	private static void addPHPLibrary(String name, String version, File libraryRoot) {
		IBuildpathEntry entry = new BuildpathEntry(IProjectFragment.K_BINARY, IBuildpathEntry.BPE_LIBRARY,
				EnvironmentPathUtils.getFullPath(EnvironmentManager.getLocalEnvironment(),
						Path.fromOSString(libraryRoot.getAbsolutePath())),
				false, BuildpathEntry.INCLUDE_ALL, BuildpathEntry.EXCLUDE_NONE, new IAccessRule[0], false,
				new IBuildpathAttribute[0], true);

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(DLTKLibraryUtils.TAG_LIBRARYVERSION, version);
		ModelManager.getUserLibraryManager().setUserLibrary(LibraryUtils.createLibraryName(name),
				new IBuildpathEntry[] { entry }, false, attributes, PHPLanguageToolkit.getDefault());
	}
}
