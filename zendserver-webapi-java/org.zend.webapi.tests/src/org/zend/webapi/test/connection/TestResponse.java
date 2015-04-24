package org.zend.webapi.test.connection;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.connection.data.AbstractResponseData;
import org.zend.webapi.core.connection.data.GenericResponseDataVisitor;
import org.zend.webapi.core.connection.data.IResponseDataVisitor;
import org.zend.webapi.core.connection.data.LicenseInfo;
import org.zend.webapi.core.connection.data.MessageList;
import org.zend.webapi.core.connection.data.ServerConfig;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.response.ResponseCode;

public class TestResponse {

	private class SimpleResponseData extends AbstractResponseData {

		public SimpleResponseData(ResponseType type, String prefix) {
			super(type, prefix, prefix.substring(prefix.lastIndexOf('/')));
		}

		public boolean accept(IResponseDataVisitor visitor) {
			return false;
		}

	}

	@Test
	public void testResponseFactory() throws FileNotFoundException,
			MalformedURLException {
		IRequest simpleRequest = null; /*new GetSystemInfoRequest(WebApiVersion.V1,
				Calendar.getInstance().getTime(), "key", "userAgent",
				"http://localhost", "secretKey");*/
//		IResponseData data = new SimpleResponseData(ResponseType.SYSTEM_INFO,
//				"prefix");
//		IResponse response = ResponseFactory.createResponse(simpleRequest,
//				ResponseCode.OK.getCode(), data);
//		Assert.assertNotNull(response);
//		Assert.assertEquals(ResponseCode.OK, response.getCode());
//		Assert.assertEquals(WebApiVersion.V1, response.getVersion());
//		Assert.assertNotNull(response.getData());
//		Assert.assertNotNull(response.getRequest());
	}

	@Test
	public void testResponseCode() {
		ResponseCode code = ResponseCode.OK;
		Assert.assertEquals(ResponseCode.OK, ResponseCode.byHttpCode(code.getCode()));
		Assert.assertEquals(ResponseCode.OK.getDescription(),
				ResponseCode.byHttpCode(code.getCode()).getDescription());
		Assert.assertEquals(ResponseCode.UNKNOWN,
				ResponseCode.byHttpCode((int) (Math.random() * 100000)));
	}

	@Test
	public void testGenericResposneDataVisitor() throws Exception {
		GenericResponseDataVisitor visitor = new GenericResponseDataVisitor();
		Assert.assertTrue(visitor.visit((LicenseInfo) null));
		Assert.assertTrue(visitor.visit((SystemInfo) null));
		Assert.assertTrue(visitor.visit((MessageList) null));
		Assert.assertTrue(visitor.visit((ServersList) null));
		Assert.assertTrue(visitor.visit((ServerInfo) null));
		Assert.assertTrue(visitor.visit((ServerConfig) null));
		Assert.assertTrue(visitor.preVisit((LicenseInfo) null));
		Assert.assertTrue(visitor.preVisit((SystemInfo) null));
		Assert.assertTrue(visitor.preVisit((MessageList) null));
		Assert.assertTrue(visitor.preVisit((ServersList) null));
		Assert.assertTrue(visitor.preVisit((ServerInfo) null));
		Assert.assertTrue(visitor.preVisit((ServerConfig) null));
	}
}
