/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.project;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.zend.sdklib.project.DeploymentScriptTypes;

/**
 * Helps wiring stream from one place to another
 */
public class InputOutputResource {

	private final InputStream is;
	private final OutputStream os;

	/**
	 * @param is
	 * @param os
	 */
	public InputOutputResource(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	/**
	 * @param scripttype
	 * @param os
	 */
	public InputOutputResource(DeploymentScriptTypes type, OutputStream os) {
		this(getInputStream(type), os);
	}

	private static InputStream getInputStream(DeploymentScriptTypes type) {
		final InputStream s = InputOutputResource.class.getResourceAsStream("scripts/"
				+ type.getFilename());
		if (s == null) {
			throw new IllegalStateException("Error finding script for " + type.getFilename());
		}
		return s;
	}

	public void copy() throws IOException {
		try {
			byte[] buf = new byte[4098];
			int c;
			while ((c = is.read(buf)) > 0) {
				os.write(buf, 0, c);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}
}