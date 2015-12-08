/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryUtils {

	private static final String RELEASE_TAG = "release"; //$NON-NLS-1$
	private static final String VERSION_TAG = "version"; //$NON-NLS-1$
	private static final String TYPE_TAG = "type"; //$NON-NLS-1$
	private static final String NAME_TAG = "name"; //$NON-NLS-1$
	private static final String PACKAGE_TAG = "package"; //$NON-NLS-1$

	public static File unzipDescriptor(File zpkFile) {
		byte[] buffer = new byte[4096];
		ZipInputStream input = null;
		FileOutputStream output = null;
		try {
			File folder = LibraryUtils.getTemp();
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
			DeploymentCore.log(e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				DeploymentCore.log(e);
			}
		}
		return null;
	}

	public static void unzipPackage(File zpkFile, File targetDir) throws IOException {
		byte[] buffer = new byte[4096];
		ZipInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new ZipInputStream(new FileInputStream(zpkFile));
			ZipEntry entry = input.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				File newFile = new File(targetDir, fileName);
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
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				DeploymentCore.log(e);
			}
		}
	}

	public static File unzipPackageToSharedFolder(String name, String version, File zpkFile) throws IOException {
		File libraryRoot = LibraryManager.getLibraryRoot(name, version);
		unzipPackage(zpkFile, libraryRoot);
		return libraryRoot;
	}

	public static Document getDeploymentDescriptor(File zpkFile)
			throws IOException, ParserConfigurationException, SAXException {
		boolean hasDescriptor = false;

		try (ZipInputStream input = new ZipInputStream(new FileInputStream(zpkFile))) {
			ZipEntry entry = input.getNextEntry();
			while (entry != null) {
				String fileName = entry.getName();
				if (!fileName.equals(DescriptorContainerManager.DESCRIPTOR_PATH)) {
					entry = input.getNextEntry();
					continue;
				}

				hasDescriptor = true;
				break;
			}
			if (!hasDescriptor)
				throw new IOException("Deployment descriptor file (deployment.xml) not found."); //$NON-NLS-1$

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			Document descriptorDocument = db.parse(input);
			return descriptorDocument;
		}
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
						return ProjectType.byName(packageNode.getTextContent().trim());
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

	public static File getTemporaryDescriptor(String name, String version) throws IOException {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("%NAME%", name); //$NON-NLS-1$
		variables.put("%VERSION%", version); //$NON-NLS-1$
		URL url = FileLocator.find(DeploymentCore.getDefault().getBundle(), new Path("resources/library_deployment.xml"), //$NON-NLS-1$
				null);
		url = FileLocator.resolve(url);
		Object content = url.getContent();
		final BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) content));
		String line;
		final StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			line = resolveVariables(line, variables);
			buffer.append(line);
			buffer.append("\n"); //$NON-NLS-1$
		}
		if (buffer.length() > 0) {
			buffer.setLength(buffer.length() - 1);
		}
		String result = buffer.toString();
		File tempFile = getTemp();
		File descFile = new File(tempFile, DescriptorContainerManager.DESCRIPTOR_PATH);
		descFile.createNewFile();
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(descFile));
		out.write(result.getBytes());
		out.close();
		return tempFile;
	}

	protected static File getTemp() {
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		File temp = new File(tempDir + File.separator + new Random().nextInt());
		temp.mkdir();
		return temp;
	}

	private static String resolveVariables(String value, Map<String, String> variables) {
		Set<String> keys = variables.keySet();
		if (keys == null) {
			return value;
		}
		for (String key : keys) {
			Pattern pattern = Pattern.compile(key);
			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				value = matcher.replaceAll(variables.get(key));
			}
		}

		return value;
	}

}
