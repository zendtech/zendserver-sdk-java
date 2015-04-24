package org.zend.webapi.test.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.request.RequestFactory;
import org.zend.webapi.core.connection.request.RequestParameter;
import org.zend.webapi.core.service.WebApiMethodType;
import org.zend.webapi.internal.core.connection.request.ConfigurationImportRequest;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;

public class TestRequest {

	@Test(expected = IllegalStateException.class)
	public void testCreateRequestInvalidURL() {
		RequestFactory.createRequest(WebApiMethodType.GET_SYSTEM_INFO,
				WebApiVersion.V1, new Date(), "keyName", "userAgent", "a:/a/",
				"123", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateRequestNullParameter() {
		ConfigurationImportRequest request = (ConfigurationImportRequest) RequestFactory
				.createRequest(WebApiMethodType.CONFIGURATION_IMPORT,
						WebApiVersion.V1, new Date(), "keyName", "userAgent",
						"http://localhost:10081", "123",
						ServerType.ZEND_SERVER_MANAGER);
		request.setConfigStream(null);
	}

	@Test
	public void testCreateRequestContentType() {
		GetSystemInfoRequest request = (GetSystemInfoRequest) RequestFactory
				.createRequest(WebApiMethodType.GET_SYSTEM_INFO,
						WebApiVersion.V1, new Date(), "keyName", "userAgent",
						"http://localhost:10081", "123",
						ServerType.ZEND_SERVER_MANAGER);

		Assert.assertNull(request.getContentType());
	}

	@Test(expected = IllegalStateException.class)
	public void testRequestParameterNullValue() {
		RequestParameter<String> requestParams = new RequestParameter<String>(
				"name", (String) null);
		requestParams.getValueAsStream();
	}

	@Test
	public void testRequestParameterFileNotExists() throws IOException {
		RequestParameter<File> requestParams = new RequestParameter<File>(
				"name", new File("test"));
		InputStream result = requestParams.getValueAsStream();
		Assert.assertTrue(result.available() > 0);
	}

	@Test
	public void testRequestParameterBoolean() throws IOException {
		RequestParameter<Boolean> requestParams = new RequestParameter<Boolean>(
				"name", Boolean.TRUE);
		InputStream result = requestParams.getValueAsStream();
		Assert.assertTrue(result.available() == 4);
	}

	@Test
	public void testRequestParameterFile() throws IOException {
		File tFile = File.createTempFile("requestParameter", "test");
		FileOutputStream stream = new FileOutputStream(tFile);
		stream.write(1);
		stream.close();
		RequestParameter<NamedInputStream> requestParams = new RequestParameter<NamedInputStream>(
				"name", new NamedInputStream(tFile));
		InputStream result = requestParams.getValueAsStream();
		Assert.assertTrue(result.available() == 1);
	}

	@Test
	public void testRequestParameterString() throws IOException {
		RequestParameter<String> requestParams = new RequestParameter<String>(
				"name", "test");
		InputStream result = requestParams.getValueAsStream();
		Assert.assertTrue(result.available() == 4);
	}
}
