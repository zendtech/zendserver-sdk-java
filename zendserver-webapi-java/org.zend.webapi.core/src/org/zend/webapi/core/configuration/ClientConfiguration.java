/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.configuration;

import java.net.URL;

/**
 * Client configuration options such as host, proxy settings, user agent
 * string, max retry attempts, etc.
 * 
 * @author Roy, 2011
 * 
 */
public class ClientConfiguration {

	/**
	 * The default HTTP user agent header for Zend Web API Java clients.
	 */
	public static final String DEFAULT_USER_AGENT = "zend-webapi-java-library";

	/**
	 * User agent name
	 */
	private String userAgent;

	/**
	 * Host name
	 */
	private URL host;

	/**
	 * @param userAgent
	 *            the user agent name
	 */
	public ClientConfiguration(String userAgent) {
		setUserAgent(userAgent);
	}

	/**
	 * Constructs a client configuration instance with default connection
	 * parameters
	 */
	public ClientConfiguration() {
		this(DEFAULT_USER_AGENT);
	}

	public ClientConfiguration(URL host) {
		this();
		setHost(host);
	}

	/**
	 * @param host
	 * @return updated client configuration
	 */
	private ClientConfiguration setHost(URL host) {
		if (host == null) {
			throw new IllegalArgumentException("host must not be null");
		}

		this.host = host;
		return this;
	}

	/**
	 * @return user agent name
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent
	 * @return updated client configuration
	 */
	public ClientConfiguration setUserAgent(String userAgent) {
		if (userAgent == null) {
			throw new IllegalArgumentException("User Agent must be not null");
		}
		this.userAgent = userAgent;
		return this;
	}

	/**
	 * @return host name
	 */
	public URL getHost() {
		return host;
	}
}
