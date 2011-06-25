/*******************************************************************************
 * Copyright (c) May 26, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.zend.sdklib.descriptor.pkg.Package;
import org.zend.sdklib.descriptor.pkg.Version;
import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.internal.library.BasicStatus;
import org.zend.sdklib.internal.mapping.ResourceMappingParser;
import org.zend.sdklib.internal.project.ProjectResourcesWriter;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.library.IChangeNotifier;
import org.zend.sdklib.library.StatusCode;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.ResourceMapper;

/**
 * Provides ability to create zpk application package based on
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class PackageBuilder extends AbstractChangeNotifier {

	private static final String EXTENSION = ".zpk";
	private static final int BUFFER = 1024;

	private ZipOutputStream out;
	private File container;

	private ResourceMapper mapper;
	private String[] defaultExclusion;

	public PackageBuilder(File file, String[] defaultExclusion,
			IChangeNotifier notifier) {
		super(notifier);
		this.container = file;
		this.defaultExclusion = defaultExclusion;
	}

	public PackageBuilder(File file, String[] defaultExclusion) {
		super();
		this.container = file;
		this.defaultExclusion = defaultExclusion;
	}

	public PackageBuilder(File file, IChangeNotifier notifier) {
		this(file, null, notifier);
	}

	public PackageBuilder(File file) {
		this(file, (String[]) null);
	}

	public PackageBuilder(String path) {
		this(new File(path));
	}

	public PackageBuilder(String path, String[] defaultExclusion) {
		this(new File(path), defaultExclusion);
	}

	public PackageBuilder(String path, String[] defaultExclusion,
			IChangeNotifier notifier) {
		this(new File(path), defaultExclusion, notifier);
	}

	public PackageBuilder(String path, IChangeNotifier notifier) {
		this(new File(path), notifier);
	}

	/**
	 * Creates compressed package file in the given folder.
	 * 
	 * @param path
	 *            - location where package should be created
	 * @return
	 */
	public File createDeploymentPackage(File location) {
		if (location == null || !location.isDirectory()) {
			log.error(new IllegalArgumentException(
					"Location cannot be null or non-existing directory"));
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

			ResourceMappingParser parser = new ResourceMappingParser();
			if (defaultExclusion != null) {
				mapper = new ResourceMapper(container, parser.load(container),
						parser.getMappings(defaultExclusion));
			} else {
				mapper = new ResourceMapper(container, parser.load(container));
			}

			notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
					"Package creation", "Creating deployment package...",
					calculateTotalWork()));
			File descriptorFile = new File(container,
					ProjectResourcesWriter.DESCRIPTOR);
			addFileToZip(descriptorFile, null, null, null);
			resolveMappings();
			out.close();
			notifier.statusChanged(new BasicStatus(StatusCode.STOPPING,
					"Package creation",
					"Deployment package created successfully."));
			return result;
		} catch (IOException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Package creation",
					"Error during building deployment package", e));
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
	 * @param path
	 *            - location where package should be created
	 * @return
	 */
	public File createDeploymentPackage() {
		return createDeploymentPackage(new File("."));
	}

	private void resolveMappings() throws IOException {
		String appdir = getAppdirName(container);
		String scriptsdir = getScriptsdirName(container);
		if (appdir != null) {
			resolveMapping("appdir", appdir);
		}
		if (scriptsdir != null) {
			resolveMapping("scriptsdir", scriptsdir);
		}
	}

	private void resolveMapping(String tag, String folderName)
			throws IOException {
		Set<IMapping> includes = mapper.getInclusion(tag);
		for (IMapping mapping : includes) {
			File resource = new File(
					new File(container, mapping.getPath()).getCanonicalPath());
			if (resource.exists()) {
				addFileToZip(resource, folderName, mapping, tag);
			}
		}
	}

	private void addFileToZip(File root, String mappingFolder,
			IMapping mapping, String tag) throws IOException {
		if (!mapper.isExcluded(root.getCanonicalPath(), tag)) {
			if (root.isDirectory()) {
				File[] children = root.listFiles();
				for (File child : children) {
					addFileToZip(child, mappingFolder, mapping, tag);
				}
			} else {
				String location = root.getCanonicalPath();
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(location), BUFFER);
				String path = getContainerRelativePath(location);
				if (mapping != null && mapping.getPath() != null) {
					path = root.getCanonicalPath();
					String fullMapping = new File(container, mapping.getPath())
							.getCanonicalPath();
					int position = 0;
					if (mapping.isContent()) {
						position = fullMapping.length();
					} else {
						position = fullMapping.lastIndexOf(File.separator);
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
				notifier.statusChanged(new BasicStatus(StatusCode.PROCESSING,
						"Package creation", "Creating deployment package...", 1));
			}
		}
	}

	private String getContainerRelativePath(String path) {
		int position = container.getAbsolutePath().length() + 1;
		return path.substring(position);
	}

	private String getPackageName(File container) {
		String result = null;
		Package p = getPackage(container);
		if (p != null) {
			String name = p.getName();
			final Version version2 = p.getVersion();
			if (version2 == null) {
				throw new IllegalStateException(
						"Error, missing <version> element in deployment descriptor");
			}

			String version = version2.getRelease();

			if (name != null && version != null) {
				result = name + "-" + version;
			}
		}
		return result;
	}

	private String getAppdirName(File container) {
		String result = null;
		Package p = getPackage(container);
		if (p != null) {
			result = p.getAppdir();
		}
		return result;
	}

	private String getScriptsdirName(File container) {
		String result = null;
		Package p = getPackage(container);
		if (p != null) {
			result = p.getScriptsdir();
		}
		return result;
	}

	private Package getPackage(File container) {
		File descriptorFile = new File(container,
				ProjectResourcesWriter.DESCRIPTOR);
		if (!descriptorFile.exists()) {
			log.error(descriptorFile.getAbsoluteFile() + " does not exist.");
			return null;
		}
		FileInputStream pkgStream = null;
		Package p = null;
		try {
			pkgStream = new FileInputStream(descriptorFile);
			p = JaxbHelper.unmarshalPackage(pkgStream);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				pkgStream.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return p;
	}

	private int calculateTotalWork() throws IOException {
		// is 1 because of deployment.xml file which is always added to the
		// package
		int totalWork = 1;
		Set<String> folders = mapper.getFolders();
		for (String folder : folders) {
			Set<IMapping> includes = mapper.getInclusion(folder);
			for (IMapping mapping : includes) {
				File resource = new File(
						new File(container, mapping.getPath())
								.getCanonicalPath());
				if (resource.exists()) {
					totalWork += countFiles(resource, folder);
				}
			}
		}
		return totalWork;
	}

	private int countFiles(File file, String folder) throws IOException {
		int counter = 0;
		if (!mapper.isExcluded(file.getCanonicalPath(), folder)) {
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				for (File child : children) {
					counter += countFiles(child, folder);
				}
			} else {
				counter++;
			}
		}
		return counter;
	}

}
