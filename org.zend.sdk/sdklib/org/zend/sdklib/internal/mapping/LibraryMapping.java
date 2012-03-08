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
	private String[] libraryPaths;

	public LibraryMapping(String folder, String[] libraryPaths) {
		super();
		this.folder = folder;
		this.libraryPaths = libraryPaths;
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
	public static LibraryMapping create(String input) {
		String[] parts = input.split("\\|");
		String folder = null;
		String[] libs = null;
		if (parts.length == 2) {
			folder = parts[0];
			libs = parts[1].split(";");
		}
		if (parts.length == 1) {
			folder = "";
			libs = parts[0].split(";");
		}
		if (folder != null && libs != null && libs.length > 0) {
			return new LibraryMapping(folder, libs);
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
	public String[] getLibraryPaths() {
		return libraryPaths;
	}

}
