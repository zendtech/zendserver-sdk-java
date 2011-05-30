/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib;

import java.io.File;
import java.io.IOException;

import org.zend.sdklib.internal.library.AbstractLibrary;
import org.zend.sdklib.internal.project.TemplateWriter;

/**
 * Sample library class
 * 
 * @author Roy, 2011
 * 
 */
public class ZendProject extends AbstractLibrary {

	protected String name;
	protected boolean withScripts;
	protected File destination;

	public ZendProject(String name, boolean withScripts, String destination) {
		this.name = name;
		this.withScripts = withScripts;
		this.destination = destination == null ? new File(".") : new File(destination);
	}

	/**
	 * Writes project to file system.
	 * 
	 * @return true on success, false otherwise.
	 */
	public boolean create() {
		TemplateWriter tw = new TemplateWriter();
		if (!destination.exists()) {
			log.error("Invalid destination path: provided location does not exist.");
			return false;
		}
		File projectRoot = new File(destination, name);
		if (!projectRoot.exists()) {
			projectRoot.mkdir();
		}
		try {
			tw.writeTemplate(name, true, withScripts, projectRoot);
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}

	public boolean update() {
		TemplateWriter tw = new TemplateWriter();
		
		try {
			tw.writeTemplate(name, false, withScripts, destination);
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
}
