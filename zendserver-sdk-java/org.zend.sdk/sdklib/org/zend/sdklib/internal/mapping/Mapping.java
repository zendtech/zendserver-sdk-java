/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import org.zend.sdklib.mapping.IMapping;

public class Mapping implements IMapping {

	private String path;
	private boolean isContent;
	private boolean isGlobal;

	public Mapping(String path, boolean isContent, boolean isGlobal) {
		super();
		this.path = path;
		this.isContent = isContent;
		this.isGlobal = isGlobal;
	}

	public String getPath() {
		return path;
	}

	public boolean isContent() {
		return isContent;
	}

	@Override
	public boolean isGlobal() {
		return isGlobal;
	}

}
