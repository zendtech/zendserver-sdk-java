/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NamedInputStream {

	/**
	 * InputStream
	 */
	private final InputStream is;

	/**
	 * a description of this input stream
	 */
	private final String name;

	/**
	 * @param is
	 * @param name
	 */
	public NamedInputStream(InputStream is, String name) {
		super();

		if (is == null) {
			throw new IllegalArgumentException(
					"InputStream must not be null (with name" + name + ")");
		}

		this.is = is;
		this.name = name;
	}

	public NamedInputStream(File file) {
		this(getStream(file), file.getName());
	}

	private static FileInputStream getStream(File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		return fileInputStream;
	}

	/**
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return is;
	}

	/**
	 * @return the name of the stream (can be file name, or any description of
	 *         the input stream)
	 */
	public String getName() {
		return name;
	}

}
