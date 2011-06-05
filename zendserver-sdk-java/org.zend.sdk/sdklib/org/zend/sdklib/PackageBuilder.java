/*******************************************************************************
 * Copyright (c) May 26, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.zend.sdklib.internal.library.AbstractLibrary;
import org.zend.sdklib.internal.project.TemplateWriter;

/**
 * Provides ability to create zpk application package based on
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class PackageBuilder extends AbstractLibrary {

	private static final String EXCLUSIONS = "exclusions";
	private static final String CONTENT = "/*";
	private static final String DEPLOYMENT_PROPERTIES = "deployment.properties";
	private static final String EXTENSION = ".zpk";
	private static final int BUFFER = 1024;

	private ZipOutputStream out;
	private File container;
	private List<String> exclusionList;
	private Map<String, String[]> mapping;

	public PackageBuilder(File file) {
		this.container = file;
		this.mapping = new HashMap<String, String[]>();
		this.exclusionList = new ArrayList<String>();
	}

	public PackageBuilder(File file, List<String> exclusionList) {
		this(file);
		this.exclusionList.addAll(exclusionList);
	}

	public PackageBuilder(String path) {
		this(new File(path));
	}

	public PackageBuilder(String path, List<String> exclusionList) {
		this(new File(path), exclusionList);
	}

	/**
	 * Creates compressed package file in the given folder.
	 * 
	 * @param destination
	 *            - location where package should be created
	 * @return
	 */
	public File createDeploymentPackage(File location) {
		if (location == null || !location.isDirectory()) {
			log.error(new IllegalArgumentException("Location cannot be null"));
			return null;
		}
		try {
			container = container.getCanonicalFile();
			String name = getPackageName(container);
			if (name == null) {
				return null;
			}
			File result = new File(location, name + EXTENSION);
			out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(result)));
			File mappingFile = new File(container, DEPLOYMENT_PROPERTIES);
			if (mappingFile.exists()) {
				mapping = getMapping(mappingFile);
				buildExclusionList();
			}
			addFileToZip(container, null, null, false);
			resolveMapping();
			out.close();
			return result;
		} catch (IOException e) {
			log.error("Error during building deployment package");
			log.error(e);
		}
		return null;
	}

	/**
	 * Creates compressed package file in the given location.
	 * 
	 * @param destination
	 *            - location where package should be created
	 * @return
	 */
	public File createDeploymentPackage(String destination) {
		return createDeploymentPackage(new File(destination));
	}

	/**
	 * Creates compressed package file in the current location.
	 * 
	 * @param destination
	 *            - location where package should be created
	 * @return
	 */
	public File createDeploymentPackage() {
		return createDeploymentPackage(new File("."));
	}

	private void resolveMapping() throws IOException {
		Set<Entry<String, String[]>> mappings = mapping.entrySet();
		for (Entry<String, String[]> entry : mappings) {
			String mapTo = entry.getKey();
			for (String file : entry.getValue()) {
				boolean isContent = file.endsWith(CONTENT);
				if (isContent) {
					file = file.trim().substring(0, file.length() - 2);
				}
				File resource = new File(container, file);
				if (resource.exists()) {
					addFileToZip(resource, mapTo, resource.getCanonicalPath(),
							isContent);
				}
			}
		}
	}

	private void buildExclusionList() throws IOException {
		Set<Entry<String, String[]>> mappings = mapping.entrySet();
		for (Entry<String, String[]> entry : mappings) {
			String mapTo = entry.getKey();
			if (EXCLUSIONS.equals(mapTo)) {
				String[] list = entry.getValue();
				for (String member : list) {
					File exclude = new File(container, member);
					exclusionList.add(exclude.getCanonicalPath());
				}
			}
		}
	}

	private boolean isExcluded(File resource) throws IOException {
		if (container.getCanonicalPath().equals(resource.getCanonicalPath())) {
			return false;
		}
		for (String exclude : exclusionList) {
			exclude = new File(container, exclude).getCanonicalPath();
			if (exclude.equals(resource.getCanonicalPath())) {
				return true;
			}
		}
		return false;
	}

	private void addFileToZip(File root, String mappingFolder,
			String rootFolder, boolean isContent) throws IOException {
		boolean isMapped = mappingFolder == null ? mapping.containsKey(root
				.getName()) : false;
		if (!isExcluded(root) && !isMapped) {
			if (root.isDirectory()) {
				File[] children = root.listFiles();
				for (File child : children) {
					addFileToZip(child, mappingFolder, rootFolder, isContent);
				}
			} else {
				String location = root.getCanonicalPath();
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(location), BUFFER);
				String path = getContainerRelativePath(location);
				if (mappingFolder != null && rootFolder != null) {
					path = root.getCanonicalPath();
					int position = 0;
					if (isContent) {
						position = rootFolder.length();
					} else {
						position = rootFolder.lastIndexOf(File.separator);
					}
					String destFolder = path.substring(position);
					path = mappingFolder + destFolder;
				}
				ZipEntry entry = new ZipEntry(path);
				out.putNextEntry(entry);
				int count;
				byte data[] = new byte[BUFFER];
				while ((count = in.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				in.close();
			}
		}
	}

	private String getContainerRelativePath(String path) {
		int position = container.getAbsolutePath().length() + 1;
		return path.substring(position);
	}

	private Map<String, String[]> getMapping(File mappingFile) {
		Properties props = new Properties();
		Map<String, String[]> result = new HashMap<String, String[]>();
		try {
			props.load(new FileReader(mappingFile));
			Enumeration<?> e = props.propertyNames();
			while (e.hasMoreElements()) {
				String folderName = (String) e.nextElement();
				String[] files = ((String) props.getProperty(folderName))
						.split(",");
				result.put(folderName, files);
			}
		} catch (IOException e) {
			log.error("Problem during reading " + DEPLOYMENT_PROPERTIES);
			log.error(e);
		}
		return result;
	}

	private String getPackageName(File container) {
		String result = null;
		File descriptorFile = new File(container, TemplateWriter.DESCRIPTOR);
		if (!descriptorFile.exists()) {
			log.error(descriptorFile.getAbsoluteFile() + " does not exist.");
			return null;
		}
		Document doc = readXMLFile(descriptorFile);
		String name = getNodeValue("/package/name", doc);
		String version = getNodeValue("/package/version/release", doc);
		if (name != null && version != null) {
			result = name + "-" + version;
		}
		return result;
	}

	private String getNodeValue(String path, Document doc) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String value = null;
		try {
			XPathExpression exp = xpath.compile(path);
			Node node = (Node) exp.evaluate(doc, XPathConstants.NODE);
			if (node != null) {
				value = node.getTextContent();
				if (value != null) {
					value = value.trim();
				}
			} else {
				log.error("Invalid descriptor file. Cannot get value of node '"
						+ path);
			}
		} catch (XPathExpressionException e) {
			log.error("Invalid descriptor file. Cannot get value of node '"
					+ path);
			log.error(e);
		}
		return value;
	}

	private Document readXMLFile(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			return db.parse(file);
		} catch (SAXException e) {
			log.error("Invalid descriptor file");
			log.error(e);
		} catch (IOException e) {
			log.error("Problem during reading descriptor file");
			log.error(e);
		} catch (ParserConfigurationException e) {
			log.error(e);
		}
		return null;
	}

}
