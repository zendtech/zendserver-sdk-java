/*******************************************************************************
 * Copyright (c) Jan 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Get information about the system, including Zend Server edition and version,
 * PHP version, licensing information etc. In general this method should be
 * available and produce similar output on all Zend Server systems, and be as
 * future compatible as possible
 * <p>
 * No parameters are required.
 * 
 * @author Roy, 2011
 * 
 */
public class GetSystemInfoRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public GetSystemInfoRequest(WebApiVersion version, Date date,
			String keyName, String userAgent, String host, String secretKey,
			ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.request.IRequest#getMethod()
	 */
	public Method getMethod() {
		return Method.GET;
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#getValidResponseCode()
	 */
	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.request.IRequest#getExpectedResponseDataType()
	 */
	public ResponseType getExpectedResponseDataType() {
		return IResponseData.ResponseType.SYSTEM_INFO;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "getSystemInfo";
	}
	
}
