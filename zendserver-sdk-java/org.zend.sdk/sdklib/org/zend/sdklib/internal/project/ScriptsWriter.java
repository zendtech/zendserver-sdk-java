/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.zend.sdklib.project.DeploymentScriptTypes;

/**
 * Helps writing scripts folder
 */
public class ScriptsWriter {

	/**
	 * Writes all deployment scripts to a given destination directory
	 * 
	 * @param dest
	 * @throws IOException
	 */
	public void writeAllScripts(File dest) throws IOException {
		if (dest == null || !dest.isDirectory()) {
			throw new IllegalArgumentException("destination directory problem");
		}
		
		for (DeploymentScriptTypes type : DeploymentScriptTypes.values()) {
			writeResource(dest, type);
		}
	}                       

	/**
	 * write specific script to a destination directory
	 * 
	 * @param dest
	 * @param type
	 * @throws IOException
	 */
	public void writeSpecificScript(File dest, DeploymentScriptTypes type)
			throws IOException {
		if (dest == null || !dest.isDirectory()) {
			throw new IllegalArgumentException("destination directory problem");
		}

		writeResource(dest, type);
	}

	private void writeResource(File dest, DeploymentScriptTypes type)
			throws IOException {

		final File file = new File(dest, type.getFilename());
		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdirs();
		}
		file.createNewFile();
		
		final FileOutputStream os = new FileOutputStream(file);
		final InputOutputResource ior = new InputOutputResource(type,
				os);
		ior.copy();
	}

}
