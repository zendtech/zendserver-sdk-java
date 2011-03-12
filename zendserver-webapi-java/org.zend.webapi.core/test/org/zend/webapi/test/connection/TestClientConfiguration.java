/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;

public class TestClientConfiguration {
	
	private static final String KEY_NAME = "studio";
	private static final String HOST = "http://il-pm1.zend.net:10081";
	private static final String SECRET_KEY = "e12204907cc386f5207966692910967640ddfcdd9a3e70abe5d460565c6b3bda";

	public static final WebApiClient getClient() throws WebApiException,
			MalformedURLException {
		/**
		 * Create the credential object
		 */
		WebApiCredentials credentials = new BasicCredentials(KEY_NAME,
				SECRET_KEY);

		/**
		 * Creates the Web API client object
		 */
		final WebApiClient webApiClient = new WebApiClient(credentials, HOST);
		return webApiClient;
	}
	
	@Test
	public void testClient() throws MalformedURLException, WebApiException {
		final WebApiClient client = getClient();
		Assert.assertNotNull(client);
	}
}
