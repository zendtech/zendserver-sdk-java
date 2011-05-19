/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Represnts a Zend Server Target environment that can be used for SDK
 * 
 * @author Roy, 2011
 */

public interface IZendTarget {

	/**
	 * @return String secret key for this target
	 */
	public abstract String getSecretKey();

	/**
	 * @return String the key of this target
	 */
	public abstract String getKey();

	/**
	 * @return URL the url of this target
	 */
	public abstract URL getHost();

	/**
	 * @return the identifier of this target
	 */
	public abstract String getId();

	/**
	 * @return the value of the given extra property key
	 */
	public abstract String getProperty(String key);

	/**
	 * @return all extra properties 
	 */
	public abstract Properties getProperties();
}
