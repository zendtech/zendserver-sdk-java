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
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;

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
	 * @param baseURL
	 */
	public HttpRepository(String id, URL url) {
		super(id);
		this.baseURL = url;
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
