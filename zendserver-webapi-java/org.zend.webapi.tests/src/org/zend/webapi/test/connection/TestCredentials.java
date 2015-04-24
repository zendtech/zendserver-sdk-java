package org.zend.webapi.test.connection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.PropertiesCredentials;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.server.utils.ServerUtils;

public class TestCredentials {

	private static final String VALID_FILE = "valid.credentials";
	private static final String INVALID_FILE = "invalid.credentials";

	@Before
	public void initClient() throws MalformedURLException, WebApiException {
		Configuration.getClient();
	}

	@Test
	public void testBasicCredentials() throws FileNotFoundException,
			MalformedURLException {
		BasicCredentials credentials = new BasicCredentials(
				Configuration.getKeyName(), Configuration.getSecretKey());
		Assert.assertEquals(Configuration.getKeyName(),
				credentials.getKeyName());
		Assert.assertEquals(Configuration.getSecretKey(),
				credentials.getSecretKey());
		final WebApiClient webApiClient = new WebApiClient(credentials,
				Configuration.getHost());
		Assert.assertNotNull(webApiClient);
	}

	@Test
	public void testValidProperties() {
		try {
			InputStream properties = readProperties(VALID_FILE);
			PropertiesCredentials credentials = new PropertiesCredentials(
					properties);
			assertNotNull(credentials.getKeyName());
			assertNotNull(credentials.getSecretKey());
		} catch (FileNotFoundException e) {
			fail("Cannot read properties file: " + VALID_FILE);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidStream() throws FileNotFoundException {
		InputStream properties = readProperties(INVALID_FILE);
		new PropertiesCredentials(properties);
	}

	private InputStream readProperties(String file)
			throws FileNotFoundException {
		InputStream properties = new BufferedInputStream(new FileInputStream(
				new File(ServerUtils.CONFIG + file)));
		return properties;
	}

}
