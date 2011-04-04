package org.zend.webapi.test.connection;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.connection.data.AbstractResponseData;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.IResponseDataVisitor;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.core.connection.response.ResponseFactory;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;

public class TestResponse {

	private class SimpleResponseData extends AbstractResponseData {

		public SimpleResponseData(ResponseType type, String prefix) {
			super(type, prefix);
		}

		public boolean accept(IResponseDataVisitor visitor) {
			return false;
		}

	}

	@Test
	public void testResponseFactory() throws FileNotFoundException,
			MalformedURLException {
		IRequest simpleRequest = new GetSystemInfoRequest(WebApiVersion.V1,
				Calendar.getInstance().getTime(), "key", "userAgent",
				"http://localhost", "secretKey");
		IResponseData data = new SimpleResponseData(ResponseType.SYSTEM_INFO,
				"prefix");
		IResponse response = ResponseFactory.createResponse(simpleRequest,
				ResponseCode.OK.getCode(), data);
		Assert.assertNotNull(response);
		Assert.assertEquals(ResponseCode.OK, response.getCode());
		Assert.assertEquals(WebApiVersion.V1, response.getVersion());
		Assert.assertNotNull(response.getData());
		Assert.assertNotNull(response.getRequest());
	}

	@Test
	public void testResponseCode() {
		ResponseCode code = ResponseCode.OK;
		Assert.assertEquals(ResponseCode.OK,
				ResponseCode.byCode(code.getCode()));
		Assert.assertEquals(ResponseCode.OK.getDescription(), ResponseCode
				.byCode(code.getCode()).getDescription());
		Assert.assertEquals(ResponseCode.UNKNOWN,
				ResponseCode.byCode((int) (Math.random() * 100000)));
	}
}
