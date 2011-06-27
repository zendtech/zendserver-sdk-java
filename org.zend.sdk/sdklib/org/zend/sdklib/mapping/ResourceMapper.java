/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps paths to resources following IResourceMapping config
 * 
 */
public class ResourceMapper {

	private IResourceMapping mapping;
	private Set<IMapping> customExclusion;
	private File container;

	public ResourceMapper(File container, IResourceMapping mapping,
			Set<IMapping> customExclusion) {
		this.mapping = mapping;
		this.customExclusion = customExclusion;
		this.container = container;
	}

	public ResourceMapper(File container, IResourceMapping mapping) {
		this(container, mapping, new HashSet<IMapping>());
	}

	/**
	 * Return mapping for specified path.
	 * 
	 * @param path
	 * @return mapped folder name
	 * @throws IOException
	 */
	public String getFolder(String path) throws IOException {
		Set<String> folders = getFolders();
		for (String folder : folders) {
			Set<IMapping> includes = getInclusion(folder);
			for (IMapping include : includes) {
				if (include.isGlobal()) {
					String fileName = path.substring(path
							.lastIndexOf(File.separator) + 1);
					if (include.getPath().equals(fileName)) {
						return folder;
					}
				} else if (include.isContent()) {
					String fullPath = new File(container, include.getPath())
							.getCanonicalPath();
					if (path.startsWith(fullPath)) {
						return folder;
					}
				} else {
					if (include.getPath().equals(path)) {
						return folder;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns inclusion list for specified folder name
	 * 
	 * @param folder
	 * @return
	 */
	public Set<IMapping> getInclusion(String folder) {
		if (folder != null) {
			Map<String, Set<IMapping>> rules = mapping.getInclusion();
			Set<IMapping> mappings = rules.get(folder);
			if (mappings != null) {
				return mappings;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Returns exclusion list for specified folder name
	 * 
	 * @param folder
	 * @return
	 */
	public Set<IMapping> getExclusion(String folder) {
		if (folder != null) {
			Map<String, Set<IMapping>> rules = mapping.getExclusion();
			Set<IMapping> mappings = rules.get(folder);
			if (mappings != null) {
				return mappings;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * @return all folder names specified in deployment.properties file
	 */
	public Set<String> getFolders() {
		return mapping.getInclusion().keySet();
	}

	/**
	 * Check if specified path is excluded for the folder
	 * 
	 * @param path
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public boolean isExcluded(String path, String folder) throws IOException {
		if (isInternalExcluded(path, mapping.getDefaultExclusion())) {
			return true;
		}
		if (isInternalExcluded(path, customExclusion)) {
			return true;
		}
		return isInternalExcluded(path, getExclusion(folder));
	}

	private boolean isInternalExcluded(String path, Set<IMapping> excludes)
			throws IOException {
		for (IMapping exclude : excludes) {
			if (exclude.isGlobal()) {
				String fileName = path.substring(path
						.lastIndexOf(File.separator) + 1);
				if (exclude.getPath().equals(fileName)) {
					return true;
				}
			} else {
				String fullPath = new File(container, exclude.getPath())
						.getCanonicalPath();
				if (fullPath.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

}
