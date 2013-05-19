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
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.internal.core.LibraryCore;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;

/**
 * Utility class for managing PHP Libraries.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryManager {

	private static final String RELEASE_TAG = "release"; //$NON-NLS-1$
	private static final String VERSION_TAG = "version"; //$NON-NLS-1$
	private static final String TYPE_TAG = "type"; //$NON-NLS-1$
	private static final String NAME_TAG = "name"; //$NON-NLS-1$
	private static final String PACKAGE_TAG = "package"; //$NON-NLS-1$

	private static final File SHARE_FOLDER;

	static {
		final String property = System.getProperty("user.home");
		final File user = new File(property);
		SHARE_FOLDER = new File(user.getAbsolutePath() + File.separator
				+ ".zend" + File.separator + "libraries");
		if (!SHARE_FOLDER.exists()) {
			SHARE_FOLDER.mkdir();
		}
	}

	/*
	 * public static String[] importZPKLibrary(File zpkFile) { File folder =
	 * unzipDescriptor(zpkFile); if (folder != null) { Document descriptor =
	 * getDeploymentDescriptor(folder); String name =
	 * getLibraryName(descriptor); String version =
	 * getLibraryVersion(descriptor); folder.deleteOnExit(); return new String[]
	 * { name, version }; } return null; }
	 */

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

	/*
	 * public static String[] importZPKLibrary(File zpkFile) { File folder =
	 * unzipDescriptor(zpkFile); if (folder != null) { Document descriptor =
	 * getDeploymentDescriptor(folder); String name =
	 * getLibraryName(descriptor); String version =
	 * getLibraryVersion(descriptor); folder.deleteOnExit(); return new String[]
	 * { name, version }; } return null; }
	 */

	public static File unzipDescriptor(File zpkFile) {
		byte[] buffer = new byte[4096];
		ZipInputStream input = null;
		FileOutputStream output = null;
		try {
			File folder = getTemp();
			input = new ZipInputStream(new FileInputStream(zpkFile));
			ZipEntry entry = input.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				File newFile = new File(folder, fileName);
				if (fileName.equals(DescriptorContainerManager.DESCRIPTOR_PATH)) {
					output = new FileOutputStream(newFile);
					int data = 0;
					while ((data = input.read(buffer)) > 0) {
						output.write(buffer, 0, data);
					}
					output.close();
					return folder;
				}
				entry = input.getNextEntry();
			}
			input.closeEntry();
			return folder;
		} catch (IOException e) {
			LibraryCore.log(e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				LibraryCore.log(e);
			}
		}
		return null;
	}

	public static File unzipPackage(String name, String version, File zpkFile) {
		byte[] buffer = new byte[4096];
		ZipInputStream input = null;
		FileOutputStream output = null;
		try {
			File folder = getLibraryRoot(version, name);
			input = new ZipInputStream(new FileInputStream(zpkFile));
			ZipEntry entry = input.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				File newFile = new File(folder, fileName);
				new File(newFile.getParent()).mkdirs();
				if (fileName.endsWith("/")) { //$NON-NLS-1$
					newFile.mkdir();
				} else {
					output = new FileOutputStream(newFile);
					int data = 0;
					while ((data = input.read(buffer)) > 0) {
						output.write(buffer, 0, data);
					}
					output.close();
				}
				entry = input.getNextEntry();
			}
			input.closeEntry();
			input.close();
			return folder;
		} catch (IOException e) {
			LibraryCore.log(e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				LibraryCore.log(e);
			}
		}
		return null;
	}

	public static File unzipToSharedFolder(String name, String version,
			File absoluteFile) {
		return unzipPackage(name, version, absoluteFile);
	}

	public static Document getDeploymentDescriptor(File path) {
		File descriptorFile = new File(path,
				DescriptorContainerManager.DESCRIPTOR_PATH);
		if (descriptorFile.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			try {
				DocumentBuilder db = factory.newDocumentBuilder();
				return db.parse(descriptorFile);
			} catch (SAXException e) {
				// TODO fail("Error during parsing configuration file: " +
				// descriptorFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO fail("Error during reading configuration file: " +
				// descriptorFile.getAbsolutePath());
			} catch (ParserConfigurationException e) {
				// TODO fail("XML parser configuration error: " +
				// descriptorFile.getAbsolutePath());
			}
		}
		return null;
	}

	public static String getLibraryName(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(PACKAGE_TAG)) {
				NodeList packageNodes = node.getChildNodes();
				for (int j = 0; j < packageNodes.getLength(); j++) {
					Node packageNode = packageNodes.item(j);
					if (packageNode.getNodeName().equals(NAME_TAG)) {
						return packageNode.getTextContent().trim();
					}
				}
			}
		}
		return null;
	}

	public static ProjectType getProjectType(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(PACKAGE_TAG)) {
				NodeList packageNodes = node.getChildNodes();
				for (int j = 0; j < packageNodes.getLength(); j++) {
					Node packageNode = packageNodes.item(j);
					if (packageNode.getNodeName().equals(TYPE_TAG)) {
						return ProjectType.byName(packageNode.getTextContent()
								.trim());
					}
				}
			}
		}
		return ProjectType.UNKNOWN;
	}

	public static String getLibraryVersion(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(PACKAGE_TAG)) {
				NodeList packageNodes = node.getChildNodes();
				for (int j = 0; j < packageNodes.getLength(); j++) {
					Node packageNode = packageNodes.item(j);
					if (packageNode.getNodeName().equals(VERSION_TAG)) {
						NodeList versionNodes = packageNode.getChildNodes();
						for (int k = 0; k < versionNodes.getLength(); k++) {
							Node versionNode = versionNodes.item(k);
							if (versionNode.getNodeName().equals(RELEASE_TAG)) {
								return versionNode.getTextContent().trim();
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static String createLibraryName(String name) {
		return name.replace('/', '-');
	}

	private static File getTemp() {
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		File temp = new File(tempDir + File.separator + new Random().nextInt());
		temp.mkdir();
		return temp;
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
				BuildpathEntry.INCLUDE_ALL, // inclusion patterns
				BuildpathEntry.EXCLUDE_NONE, // exclusion patterns
				new IAccessRule[0], false, // no access rules to combine
				new IBuildpathAttribute[0], true);
		ModelManager.getUserLibraryManager().setUserLibrary(
				createLibraryName(name), version,
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
		File libFile = new File(SHARE_FOLDER, relativePath);
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

	private static File getLibraryRoot(String version, String name) {
		File libFolder = new File(SHARE_FOLDER, name);
		File versionFolder = new File(libFolder, version);
		if (!versionFolder.exists()) {
			versionFolder.mkdirs();
		}
		return versionFolder;
	}

	private static boolean isExist(String version, String name) {
		File libFolder = new File(SHARE_FOLDER, name);
		if (libFolder.exists()) {
			File versionFolder = new File(libFolder, version);
			return versionFolder.exists();
		}
		return false;
	}

}
