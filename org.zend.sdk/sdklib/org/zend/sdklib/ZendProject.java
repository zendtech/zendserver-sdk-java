/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib;

import org.zend.sdklib.internal.library.AbstractLibrary;

/**
 * Sample library class
 * 
 * @author Roy, 2011
 * 
 */
public class ZendProject extends AbstractLibrary {

	protected String name;
	protected String target;
	protected String index;
	protected String path;

	public ZendProject(String name, String target, String index, String path) {
		this.name = name;
		this.target = target;
		this.index = index;
		this.path = path;
	}

	public boolean create() {
		// TODO create project based on fields values
		return true;
	}

}
