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
public class Target {
	

	final private String id;
	final private URL host;
	final private String key;
	final private String secretKey;

	/**
	 * @param id
	 * @param host
	 * @param key
	 * @param secretKey
	 */
	public Target(String id, URL host, String key, String secretKey) {
		super();
		this.id = id;
		this.host = host;
		this.key = key;
		this.secretKey = secretKey;
	}

	/**
	 * @return the identifier of this target
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return URL the url of this target
	 */
	public URL getHost() {
		return host;
	}
	
	/**
	 * @return String the key of this target
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return String secret key for this target
	 */
	public String getSecretKey() {
		return secretKey;
	}

}
