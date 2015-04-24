/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.repository.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.zend.sdklib.internal.repository.AbstractRepository;

/**
 * Http based repository
 * 
 * @author Roy, 2011
 * 
 */
public class HttpRepository extends AbstractRepository {

	private final URL baseURL;

	/**
	 * Base URL of this application site
	 * 
	 * @param id
	 * @param name
	 * @param url
	 */
	public HttpRepository(String id, String name, URL url) {
		super(id, name);
		this.baseURL = url;
	}

	/**
	 * Base URL of this application site
	 * 
	 * @param url
	 * @param name
	 * 
	 * @param baseURL
	 */
	public HttpRepository(String id, String name) {
		this(id, name, formURL(id));
	}

	private static URL formURL(String id) {
		try {
			return new URL(id);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(id + " is not a valid URL");
		}
	}

	@Override
	public InputStream getArtifactStream(String path) throws IOException {
		URL url = new URL(this.baseURL.toString() + "/" + path);
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}

	@Override
	public boolean isAccessible() {
		try {
			InputStream s = getArtifactStream(SITE_XML);
			return s.read() != -1;
		} catch (IOException e) {
			return false;
		}
	}
}
