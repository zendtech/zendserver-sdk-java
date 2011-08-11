/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.response;

import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Basic implementation of the {@link IResponse} method
 * 
 * @author Roy, 2011
 * 
 */
public class Response implements IResponse {

	private final IRequest request;
	private ResponseCode code;
	private WebApiVersion version;
	
	protected final IResponseData data;

	public Response(IRequest request, int responseCode, IResponseData info) {
		this.code = ResponseCode.byHttpCode(responseCode);
		this.request = request;
		this.data = info;
		this.version = request.getVersion();
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.response.IResponse#getRequest()
	 */
	public IRequest getRequest() {
		return request;
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.response.IResponse#getCode()
	 */
	public ResponseCode getCode() {
		return code;
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.response.IResponse#getVersion()
	 */
	public WebApiVersion getVersion() {
		return version;
	}
	
	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.response.IResponse#getData()
	 */
	public IResponseData getData() {
		return data;
	}
}
