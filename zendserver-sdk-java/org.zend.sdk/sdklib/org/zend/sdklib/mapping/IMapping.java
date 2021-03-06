/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

/**
 * Defines mapping from one file to the another one.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMapping {

	/**
	 * @return path the file to map
	 */
	public String getPath();

	/**
	 * @return if the mapping should be applied globally - for each folder then
	 *         returns <code>true</code>; otherwise returns <code>false</code>
	 */
	public boolean isGlobal();

	/**
	 * Set path to the file which should me mapped.
	 * 
	 * @param path
	 */
	public void setPath(String path);

	/**
	 * Set <code>true</code> if mapping should be consider globally
	 * 
	 * @param value
	 */
	public void setGlobal(boolean value);

}
