/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.net.URL;


/**
 * Represents a target in the environment
 * @author Roy, 2011
 */
public interface ITarget {

	
	/**
	 * @return URL the url of this target
	 */
	public URL getHost(); 
	
	/**
	 * @return String the key of this target
	 */
	public String getKey();

	/**
	 * @return String secret key for this target
	 */
	public String getSecretKey();

}
