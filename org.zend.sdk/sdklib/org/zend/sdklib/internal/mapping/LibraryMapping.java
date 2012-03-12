/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

/**
 * Represents library mapping which allows to include libraries into deployment
 * package.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class LibraryMapping {

	private String folder;
	private String path;

	public LibraryMapping(String folder, String path) {
		super();
		this.folder = folder;
		this.path = path;
	}

	/**
	 * Creates {@link LibraryMapping} instance based on provided input string.
	 * Valid input syntax is:
	 * <p>
	 * folder_name|path/to/library1;path/to/library2...
	 * </p>
	 * 
	 * @param input
	 *            string
	 * @return instance of {@link LibraryMapping}
	 */
	public static LibraryMapping create(String name, String path) {
		if (!"library".equals(name)) {
			return null;
		}
		String[] parts = path.split(";");
		String folder = null;
		String lib = null;
		if (parts.length == 2) {
			folder = parts[0];
			lib = parts[1];
		}
		if (parts.length == 1) {
			folder = "";
			lib = parts[0];
		}
		if (folder != null && lib != null) {
			return new LibraryMapping(folder, lib);
		}
		return null;
	}

	/**
	 * @return name of the folder in which specified libraries should be located
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @return array of paths to libraries
	 */
	public String getLibraryPath() {
		return path;
	}

}
