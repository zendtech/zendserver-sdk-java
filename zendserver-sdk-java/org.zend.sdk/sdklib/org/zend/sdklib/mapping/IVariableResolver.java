/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import org.zend.sdklib.SdkException;

/**
 * Implementors should provide mechanism to resolve variables in file paths.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IVariableResolver {

	/**
	 * Replace all variables with their values in provided path.
	 * 
	 * @param path
	 * @return path with replaced variables
	 * @throws SdkException
	 */
	String resolve(String path) throws SdkException;

}
