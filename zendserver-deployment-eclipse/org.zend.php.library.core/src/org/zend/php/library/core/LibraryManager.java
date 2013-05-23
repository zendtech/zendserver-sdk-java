/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.osgi.framework.Bundle;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.internal.core.LibraryCore;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;

/**
 * Utility class for managing PHP Libraries.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryManager {

	private static final String SHARED_FOLDER_NAME = "libraries"; //$NON-NLS-1$
	private static final File SHARED_FOLDER;

	static {
		Bundle bundle = Platform.getBundle(LibraryCore.PLUGIN_ID);
		SHARED_FOLDER = Platform.getStateLocation(bundle)
				.append(SHARED_FOLDER_NAME).toFile();
		if (!SHARED_FOLDER.exists()) {
			SHARED_FOLDER.mkdirs();
		}
	}

	public static void addDeployableLibrary(LibraryDeployData data) {
		addLibrary(data.getName(), data.getVersion(), data.getRoot());
	}

	public static void addDeployableLibrary(IProject project) {
		IDescriptorContainer descContainer = DescriptorContainerManager
				.getService().openDescriptorContainer(project);
		IDeploymentDescriptor descModel = descContainer.getDescriptorModel();
		String version = descModel.getReleaseVersion();
		String name = descModel.getName();
		addLibrary(name, version, project.getLocation().toFile()
				.getAbsoluteFile());
	}

	public static void addLibrary(String name, String version, File root) {
		if (!isExist(version, name)) {
			copyToSharedFolder(name, version, root);
		}
		addPHPLibrary(name, version);
	}

	public static void addLibraryToProject(File destination, File source,
			String name) {
		File destFile = new File(destination, name);
		if (!destFile.exists()) {
			try {
				copy(source, destFile, source.getAbsolutePath().length());
			} catch (IOException e) {
				LibraryCore.log(e);
			}
		}
	}

	public static File getSharedFolder() {
		return SHARED_FOLDER;
	}

	protected static File getLibraryRoot(String version, String name) {
		File libFolder = new File(SHARED_FOLDER, name);
		File versionFolder = new File(libFolder, version);
		if (!versionFolder.exists()) {
			versionFolder.mkdirs();
		}
		return versionFolder;
	}

	@SuppressWarnings("restriction")
	private static void addPHPLibrary(String name, String version) {
		String[] names = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		for (String n : names) {
			if (n.equals(name)) {
				return;
			}
		}
		IBuildpathEntry entry = new BuildpathEntry(IProjectFragment.K_BINARY,
				IBuildpathEntry.BPE_LIBRARY, EnvironmentPathUtils.getFullPath(
						EnvironmentManager.getLocalEnvironment(), Path
								.fromOSString(getLibraryRoot(version, name)
										.getAbsolutePath())), false,
				BuildpathEntry.INCLUDE_ALL, BuildpathEntry.EXCLUDE_NONE,
				new IAccessRule[0], false, new IBuildpathAttribute[0], true);
		ModelManager.getUserLibraryManager().setUserLibrary(
				LibraryUtils.createLibraryName(name), version,
				new IBuildpathEntry[] { entry }, false,
				PHPLanguageToolkit.getDefault());
	}

	private static void copyToSharedFolder(String name, String version,
			File absoluteFile) {
		try {
			copy(absoluteFile, absoluteFile, name, version);
		} catch (IOException e) {
			LibraryCore.log(e);
		}
	}

	private static void copy(File lib, File vendor, String packageName,
			String version) throws IOException {
		String relativePath = lib.getAbsolutePath().substring(
				vendor.getAbsolutePath().length());
		if (version != null) {
			relativePath = packageName + File.separator + version
					+ relativePath;
		}
		File libFile = new File(SHARED_FOLDER, relativePath);
		if (lib.isDirectory()) {
			if (!libFile.exists()) {
				libFile.mkdirs();
			}
			File[] files = lib.listFiles();
			for (File file : files) {
				copy(file, vendor, packageName, version);
			}
		} else {
			if (!libFile.exists()) {
				libFile.createNewFile();
			}
			InputStream in = new FileInputStream(lib);
			OutputStream out = new FileOutputStream(libFile);
			copyInputStream(in, out);
		}
	}

	private static void copy(File sourceFile, File destFile, int root)
			throws IOException {
		String relativePath = sourceFile.getAbsolutePath().substring(root);
		File file = new File(destFile, relativePath);
		if (sourceFile.isDirectory()) {
			if (!file.exists()) {
				file.mkdirs();
			}
			File[] files = sourceFile.listFiles();
			for (File f : files) {
				copy(f, destFile, root);
			}
		} else {
			if (!file.exists()) {
				file.createNewFile();
			}
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(file);
			copyInputStream(in, out);
		}
	}

	/**
	 * Copies streams.
	 * 
	 * @param in
	 *            Input stream to copy from
	 * @param out
	 *            Output stream to copy to
	 * @param size
	 * @throws IOException
	 */
	private static boolean copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[4096];
		int len;
		try {
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				LibraryCore.log(e);
			}
		}
		return true;
	}

	private static boolean isExist(String version, String name) {
		File libFolder = new File(SHARED_FOLDER, name);
		if (libFolder.exists()) {
			File versionFolder = new File(libFolder, version);
			return versionFolder.exists();
		}
		return false;
	}

	public static boolean isExcludedFromBuildpath(IProject project, IPath path) {
		IBuildpathEntry[] buildpathEntries = DLTKCore.create(project)
				.readRawBuildpath();
		for (int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry curr = buildpathEntries[i];
			if (curr.getEntryKind() == IBuildpathEntry.BPE_SOURCE
					&& curr.getPath()
							.equals(project.getProject().getFullPath())) {
				IPath[] exclusionPatterns = curr.getExclusionPatterns();
				for (IPath p : exclusionPatterns) {
					if (path.matchingFirstSegments(p) == p.segmentCount()) {
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	public static void excludeFromBuildpath(IScriptProject fCurrProject,
			Path path, IProgressMonitor monitor)
			throws ModelException {
		IBuildpathEntry[] buildpathEntries = fCurrProject.readRawBuildpath();
		IBuildpathEntry toModify = null;
		List<IBuildpathEntry> newRawBuildpath = new ArrayList<IBuildpathEntry>();
		for (int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry curr = buildpathEntries[i];
			if (curr.getEntryKind() == IBuildpathEntry.BPE_SOURCE
					&& curr.getPath().equals(
							fCurrProject.getProject().getFullPath())) {
				toModify = curr;
			} else {
				newRawBuildpath.add(curr);
			}
		}
		List<IPath> exlusionPatters = new ArrayList<IPath>(
				Arrays.asList(toModify.getExclusionPatterns()));
			exlusionPatters.add(path);
		IBuildpathEntry newEntry = DLTKCore.newSourceEntry(toModify.getPath(),
				toModify.getInclusionPatterns(),
				exlusionPatters.toArray(new IPath[exlusionPatters.size()]),
				toModify.getExtraAttributes());
		newRawBuildpath.add(newEntry);
		fCurrProject.setRawBuildpath(newRawBuildpath
				.toArray(new IBuildpathEntry[newRawBuildpath.size()]), monitor);
	}
}
