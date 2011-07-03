/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import org.zend.sdklib.mapping.IMapping;

/**
 * Basic implementation of {@link IMapping}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#isContent()
	 */
	@Override
	public boolean isContent() {
		return isContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#isGlobal()
	 */
	@Override
	public boolean isGlobal() {
		return isGlobal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#setPath(java.lang.String)
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#setContent(boolean)
	 */
	@Override
	public void setContent(boolean value) {
		this.isContent = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMapping#setGlobal(boolean)
	 */
	@Override
	public void setGlobal(boolean value) {
		this.isGlobal = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMapping) {
			IMapping objMapping = (IMapping) obj;
			if (objMapping.getPath().equals(getPath())) {
				return true;
			}
		}
		return false;
	}
}
