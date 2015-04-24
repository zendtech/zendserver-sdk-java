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
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

/**
 * Represents a Zend Server Target environment that can be used for SDK
 * 
 * @author Roy, 2011
 */

public interface IZendTarget {
	
	public static final String SERVER_VERSION = "serverVersion";
	public static final String OPERATING_SYSTEM = "operatingSystem";

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
	 * @return associated PHP server name
	 */
	String getServerName();

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
	 * @throws LicenseExpiredException
	 */
	boolean connect() throws WebApiException, LicenseExpiredException;
	
	/**
	 * Try to connect with this target using specified WebAPI version.
	 * 
	 * @param webapi
	 *            version
	 * @param serverType
	 * @return true if connection success
	 * @throws WebApiException
	 * @throws LicenseExpiredException
	 */
	boolean connect(WebApiVersion version, ServerType serverType)
			throws WebApiException, LicenseExpiredException;

	/**
	 * @return true if target was not fully initialized and e.g. requires some additional
	 * operations before connecting to. False otherwise
	 */
	boolean isTemporary();
	
	ServerType getServerType();
	
	WebApiVersion getWebApiVersion();
	
}
