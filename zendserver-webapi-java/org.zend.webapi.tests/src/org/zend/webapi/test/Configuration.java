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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.PropertiesCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.test.server.utils.ServerType;

public class Configuration {

	private static final String PROPERTY = "org.zend.webapi.confgurationFile";
	private static ServerType type;
	private static SystemEdition edition;
	private static String host;

	private static String configFile = "configuration.properties";

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
			switch (edition) {
			case ZEND_SERVER_CLUSER_MANAGER:
			case ZEND_SERVER_COMMUNITY_EDITION:
				webApiClient
						.setServerType(org.zend.webapi.core.connection.data.values.ServerType.ZEND_SERVER_MANAGER);
				break;
			default:
				webApiClient
						.setServerType(org.zend.webapi.core.connection.data.values.ServerType.ZEND_SERVER);
				break;
			}
		}
		return webApiClient;
	}

	public static void clean() {
		webApiClient = null;
	}

	public static ServerType getType() {
		return type;
	}

	public static SystemEdition getEdition() {
		return edition;
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
			String file = System.getProperty(PROPERTY);
			if (file != null) {
				configFile = file;
			}
			InputStream stream = new BufferedInputStream(new FileInputStream(
					new File(configFile)));
			p.load(stream);
			stream.close();
			stream = new BufferedInputStream(new FileInputStream(new File(
					configFile)));
			credentials = new PropertiesCredentials(stream);
			stream.close();
		} catch (Exception e) {
			fail("Error during reading " + configFile);
		}
		type = ServerType.byType((String) p.get("serverType"));
		host = (String) p.get("host");
		try {
			URI uri = new URI(host);
			int port = uri.getPort();
			while (!available(port)) {
				port++;
			}
			host = new URL(uri.getScheme(), uri.getHost(), port, "").toString();
			System.out.println(host);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}

		edition = SystemEdition.byName((String) p.getProperty("systemEdition"));

		if (type == null || host == null) {
			fail("missing entries type and/or host in " + configFile);
		}
		if (type.equals(ServerType.UNKNOWN.getType())) {
			fail("Incorrect server type. Allowed values are EXTERNAL or EMBEDDED.");
		}
		return credentials;
	}

	private static boolean available(int port) {
		Socket s = null;
		try {
			s = new Socket("localhost", port);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw new RuntimeException("You should handle this error.",
							e);
				}
			}
		}
	}

}
