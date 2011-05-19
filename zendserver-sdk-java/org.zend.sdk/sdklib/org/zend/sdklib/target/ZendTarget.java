/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.net.URL;
import java.util.Properties;


/**
 * Represents a target in the environment
 * @author Roy, 2011
 */
public class ZendTarget  implements IZendTarget {

	final private String id;
	final private URL host;
	final private String key;
	final private String secretKey;
	final private Properties properties;

	/**
	 * @param id
	 * @param host
	 * @param key
	 * @param secretKey
	 */
	public ZendTarget(String id, URL host, String key, String secretKey) {
		super();
		this.id = id;
		this.host = host;
		this.key = key;
		this.secretKey = secretKey;
		this.properties = new Properties();
	}

	@Override
	public String getId() {
		return id;
	}
	

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#getHost()
	 */
	@Override
	public URL getHost() {
		return host;
	}
	
	
	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#getSecretKey()
	 */
	@Override
	public String getSecretKey() {
		return secretKey;
	}

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Adds an extra property to the target
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#getAll()
	 */
	@Override
	public Properties getProperties() {
		return this.properties;
	}
}
