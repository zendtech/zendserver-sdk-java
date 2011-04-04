/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.PropertiesCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.test.server.utils.ServerType;

public class Configuration {

	private static ServerType type;
	private static String host;

	private static final String CONFIG_FILE = "configuration.properties";

	private static String keyName;
	private static String secretKey;
	private static WebApiClient webApiClient;

	private Configuration() {
	}

	public static final WebApiClient getClient() throws WebApiException,
			MalformedURLException {
		if (webApiClient == null) {
			WebApiCredentials credentials = readTestConfiguration();
			keyName = credentials.getKeyName();
			secretKey = credentials.getSecretKey();
			webApiClient = new WebApiClient(credentials, host);
		}
		return webApiClient;
	}

	public static ServerType getType() {
		return type;
	}

	public static String getHost() {
		return host;
	}

	public static String getKeyName() {
		return keyName;
	}

	public static String getSecretKey() {
		return secretKey;
	}

	private static WebApiCredentials readTestConfiguration() {
		WebApiCredentials credentials = null;
		Properties p = new Properties();
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(
					new File(CONFIG_FILE)));
			p.load(stream);
			stream.close();
			stream = new BufferedInputStream(new FileInputStream(new File(
					CONFIG_FILE)));
			credentials = new PropertiesCredentials(stream);
			stream.close();
		} catch (Exception e) {
			fail("Error during reading " + CONFIG_FILE);
		}
		type = ServerType.byType((String) p.get("serverType"));
		host = (String) p.get("host");
		if (type == null || host == null) {
			fail("missing entries type and/or host in " + CONFIG_FILE);
		}
		if (type.equals(ServerType.UNKNOWN.getType())) {
			fail("Incorrect server type. Allowed values are EXTERNAL or EMBEDDED.");
		}
		return credentials;
	}

}
