/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.zend.webapi.core.WebApiException;

/**
 * Represents a Zend Server Target environment that can be used for SDK
 * 
 * @author Roy, 2011
 */

public interface IZendTarget {

	/**
	 * @return String secret key for this target
	 */
	String getSecretKey();

	/**
	 * @return String the key of this target
	 */
	String getKey();

	/**
	 * @return URL the url of this target
	 */
	URL getHost();

	/**
	 * @return default server URL
	 */
	URL getDefaultServerURL();

	/**
	 * @return the identifier of this target
	 */
	String getId();

	/**
	 * @return the value of the given extra property key
	 */
	String getProperty(String key);

	/**
	 * @return the value of the given extra property key
	 */
	void load(InputStream is) throws IOException;

	/**
	 * @return the value of the given extra property key
	 */
	void store(OutputStream os) throws IOException;

	/**
	 * @return true if connection success
	 * @throws WebApiException
	 */
	boolean connect() throws WebApiException;
}
