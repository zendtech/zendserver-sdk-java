package org.zend.webapi.test.core;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.ServiceDispatcher;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;
import org.zend.webapi.internal.core.connection.exception.InternalWebApiException;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;

public class TestServiceDispatcher extends AbstractTestServer {

	// client resource which handle() returns null
	private class SimpleServiceDispatcher extends ServiceDispatcher {
		@Override
		protected ClientResource getResource(IRequest webApiRequest)
				throws SignatureException {

			class MyResource extends ClientResource {
				@Override
				public Representation handle() {
					return null;
				}
			}
			return new MyResource();
		}
	}

	// client resource which throws ResourceException
	private class SimpleServiceDispatcher2 extends ServiceDispatcher {
		@Override
		protected ClientResource getResource(IRequest webApiRequest)
				throws SignatureException {

			class MyResource extends ClientResource {
				@Override
				public Representation handle() {
					throw new ResourceException(1);
				}
			}
			return new MyResource();
		}
	}

	@Test
	public void testDispatch() throws WebApiException {
		initMock(handler.getSystemInfo(), "getSystemInfo", ResponseCode.OK);
		IRequest simpleRequest = new GetSystemInfoRequest(WebApiVersion.V1,
				Calendar.getInstance().getTime(), Configuration.getKeyName(),
				"userAgent", Configuration.getHost(),
				Configuration.getSecretKey());
		ServiceDispatcher dispatcher = new ServiceDispatcher();
		IResponse response = dispatcher.dispatch(simpleRequest);
		Assert.assertNotNull(response);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDispatchNullRequest() throws WebApiException {
		ServiceDispatcher dispatcher = new ServiceDispatcher();
		dispatcher.dispatch(null);
	}

	@Test(expected = WebApiCommunicationError.class)
	public void testDispatchNullResource() throws WebApiException {
		initMock(handler.getSystemInfo(), "getSystemInfo", ResponseCode.OK);
		IRequest simpleRequest = new GetSystemInfoRequest(WebApiVersion.V1,
				Calendar.getInstance().getTime(), Configuration.getKeyName(),
				"userAgent", Configuration.getHost(),
				Configuration.getSecretKey());
		ServiceDispatcher dispatcher = new SimpleServiceDispatcher();
		dispatcher.dispatch(simpleRequest);
	}

	@Test(expected = InternalWebApiException.class)
	public void testDispatchResourceException() throws WebApiException {
		initMock(handler.getSystemInfo(), "getSystemInfo", ResponseCode.OK);
		IRequest simpleRequest = new GetSystemInfoRequest(WebApiVersion.V1,
				Calendar.getInstance().getTime(), Configuration.getKeyName(),
				"userAgent", Configuration.getHost(),
				Configuration.getSecretKey());
		ServiceDispatcher dispatcher = new SimpleServiceDispatcher2();
		dispatcher.dispatch(simpleRequest);
	}

}
