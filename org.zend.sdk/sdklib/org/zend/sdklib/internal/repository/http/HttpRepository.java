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

	private final String baseURL;

	/**
	 * Base URL of this application site
	 * 
	 * @param baseURL
	 */
	public HttpRepository(String baseURL) {
		this.baseURL = baseURL;

		try {
			new URL(baseURL);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public InputStream getSiteStream() throws IOException {
		return getArtifactStream("site.xml");
	}

	@Override
	public InputStream getArtifactStream(String path) throws IOException {
		URL url = new URL(this.baseURL + "/" + path);
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}
}
