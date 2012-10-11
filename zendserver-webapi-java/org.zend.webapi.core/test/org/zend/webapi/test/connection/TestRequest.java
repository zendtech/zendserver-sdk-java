package org.zend.webapi.test.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.request.RequestFactory;
import org.zend.webapi.core.connection.request.RequestParameter;
import org.zend.webapi.core.service.WebApiMethodType;
import org.zend.webapi.internal.core.connection.request.ConfigurationImportRequest;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;
import org.zend.webapi.internal.core.connection.request.HeaderParameters;

public class TestRequest {

	@Test(expected = IllegalStateException.class)
	public void testCreateRequestInvalidURL() {
		RequestFactory.createRequest(WebApiMethodType.GET_SYSTEM_INFO,
				WebApiVersion.V1, new Date(), "keyName", "userAgent", "a:/a/",
				"123", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateRequestNullParameter() {
		ConfigurationImportRequest request = null;/*(ConfigurationImportRequest) RequestFactory
				.createRequest(WebApiMethodType.CONFIGURATION_IMPORT,
						WebApiVersion.V1, new Date(), "keyName", "userAgent",
						"http://localhost:10081", "123");*/
//		request.setConfigStream(null);
	}

	@Test
	public void testCreateRequestContentType() {
		GetSystemInfoRequest request = null;/*(GetSystemInfoRequest) RequestFactory
				.createRequest(WebApiMethodType.GET_SYSTEM_INFO,
						WebApiVersion.V1, new Date(), "keyName", "userAgent",
						"http://localhost:10081", "123");*/
//		Assert.assertNull(request.getContentType());
	}

	@Test
	public void testHeaderParameters() {
		HeaderParameters headerParameters = new HeaderParameters();
		List<Parameter> params = new ArrayList<Parameter>();
		for (int i = 0; i < 5; i++) {
			Parameter p = headerParameters.createEntry(String.valueOf(i),
					"test");
			Assert.assertNotNull(p);
			params.add(p);
		}
		Series<Parameter> series = headerParameters.createSeries(params);
		Assert.assertNotNull(series);
	}

	@Test
	public void testHeaderParametersNull() {
		HeaderParameters headerParameters = new HeaderParameters();
		Series<Parameter> series = headerParameters.createSeries(null);
		Assert.assertNull(series);
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
