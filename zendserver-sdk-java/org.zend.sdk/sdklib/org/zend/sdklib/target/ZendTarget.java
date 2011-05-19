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
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

/**
 * Represents a target in the environment
 * 
 * @author Roy, 2011
 */
/**
 * @author roy
 * 
 */
public class ZendTarget implements IZendTarget {

	private String id;
	private URL host;
	private String key;
	private String secretKey;
	private Properties properties;

	/**
	 * Mainly used for loading
	 */
	public ZendTarget() {
		// empty target
	}
	
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getHost()
	 */
	@Override
	public URL getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getSecretKey()
	 */
	@Override
	public String getSecretKey() {
		return secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		return properties.getProperty("extra." + key);
	}

	/**
	 * Adds an extra property to the target
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value) {
		properties.put("extra." + key, value);
	}

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream is) throws IOException {
		Properties properties = new Properties();
		properties.load(is);
		this.id = properties.getProperty("_id");
		this.key = properties.getProperty("_key");
		this.secretKey = properties.getProperty("_secretKey");
		this.host = new URL(properties.getProperty("_host"));
		final Set<String> stringPropertyNames = properties
				.stringPropertyNames();
		for (String keyName : stringPropertyNames) {
			if (keyName.startsWith(".extra.")) {
				this.properties.put(keyName, properties.getProperty(keyName));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.sdklib.target.IZendTarget#store(java.io.OutputStream)
	 */
	@Override
	public void store(OutputStream os) throws IOException {
		Properties properties = new Properties();
		properties.put("_id", getId());
		properties.put("_key", getKey());
		properties.put("_secretKey", getSecretKey());
		properties.put("_host", getHost().toString());
		properties.putAll(properties);
		properties.store(os, "target properties for " + getId());
	}
}
