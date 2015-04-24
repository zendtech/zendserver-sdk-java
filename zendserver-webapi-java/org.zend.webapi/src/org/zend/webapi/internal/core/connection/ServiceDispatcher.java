/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Uniform;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.DataDigster;
import org.zend.webapi.core.connection.dispatch.IServiceDispatcher;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.request.RequestParameter;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.connection.response.ResponseFactory;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;
import org.zend.webapi.internal.core.connection.exception.InternalWebApiException;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * WebApi Service Dispatcher
 * 
 * @author Roy, 2011
 * 
 */
public class ServiceDispatcher implements IServiceDispatcher {

	private Context context;

	public ServiceDispatcher() {
		this(null);
	}

	public ServiceDispatcher(Context context) {
		this.context = context;
	}

	public IResponse dispatch(IRequest request) throws WebApiException {

		if (request == null) {
			throw new IllegalArgumentException("Error request == null");
		}

		IResponse response = null;
		try {

			// processing the request
			ClientResource resource = getResource(request);

			// getting the low-level response representation
			final Representation handle = resource.handle();
			if (handle == null) {
				WebApiException e = new WebApiCommunicationError();
				WebApiClient.logError(e);
				throw e;
			}
			/*try {
				System.out.println(handle.getText());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			final DataDigster dataDigster = new DataDigster(request, handle);
			// digest response
			final Status status = resource.getStatus();
			int responseCode = status.getCode();

			WebApiClient.logInfo("sent " + request.getClass().getSimpleName());
			WebApiClient.logInfo("resposne code  = " + responseCode);

			if (!request.isExpectedResponseCode(responseCode)) {
				UnexpectedResponseCode ex = new UnexpectedResponseCode(
						responseCode, handle);
				WebApiClient.logError(ex.getResponseCode().getErrorCode()
						+ " = " + ex.getResponseCode().getDescription());
				throw ex;
			}

			dataDigster.digest();

			// creating the response object
			response = ResponseFactory.createResponse(request, responseCode,
					dataDigster.getResponseData());

		} catch (ResourceException e) {
			throw new InternalWebApiException(e);
		}

		return response;
	}

	protected ClientResource getResource(IRequest webApiRequest)
			throws SignatureException {

		final Request request = createRequest(webApiRequest);
		final ClientResource clientResource = new ClientResource(request,
				new Response(null));

		if (context != null) {
			Uniform uniform = clientResource.getNext();
			if (uniform instanceof Restlet) {
				Restlet restlet = (Restlet) uniform;
				restlet.setContext(context);
			}
		}

		return clientResource;
	}

	/**
	 * Creates the low level request to the Reslet framework
	 * 
	 * @param webApiRequest
	 * @throws SignatureException
	 */
	private Request createRequest(IRequest webApiRequest)
			throws SignatureException {

		Request request = new Request();

		request.setProtocol(getProtocol(webApiRequest.getHost()));

		// reference
		final Reference baseRef = new Reference(webApiRequest.getHost());
		final Reference reference = new Reference(baseRef.toString()
				+ webApiRequest.getUri());
		request.setResourceRef(reference);

		// method
		final Method method = webApiRequest.getMethod();
		request.setMethod(method);

		// user agent
		final ClientInfo clientInfo = new ClientInfo();
		clientInfo.setAgent(webApiRequest.getUserAgent());
		final ArrayList<Preference<MediaType>> arrayList = new ArrayList<Preference<MediaType>>(
				1);

		final String name = "application/vnd.zend.serverapi+xml;version="
				+ webApiRequest.getVersion().getVersionName();
		arrayList.add(new Preference<MediaType>(new MediaType(name)));

		clientInfo.setAcceptedMediaTypes(arrayList);
		request.setClientInfo(clientInfo);

		// date
		request.setDate(webApiRequest.getDate());

		// signature
		StringBuilder b = new StringBuilder(webApiRequest.getKeyName());
		b.append("; ");
		b.append(webApiRequest.getSignature());
		Series<Header> headers = new Series<Header>(Header.class);
		headers.add("X-Zend-Signature", b.toString());
		request.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, headers);
		// timeout
		request.getAttributes().put(IRequest.TIMEOUT,
				webApiRequest.getTimeout());

		// host
		request.setHostRef(baseRef.getAuthority());

		// other parameters
		final List<RequestParameter<?>> parameters = webApiRequest
				.getParameters();
		if (parameters != null) {
			webApiRequest.applyParameters(request);
		}

		return request;
	}

	private Protocol getProtocol(String host) {
		if (host.startsWith("https")) {
			return Protocol.HTTPS;
		}
		return Protocol.HTTP;
	}
}
