/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Retrieve information about a particular request's events and code tracing.
 * The requestUid identifier is provided in a cookie that is set in the response
 * to the particular request. This API action is designed to be used with the
 * new Studio browser toolbar.
 * <p>
 * Limitations:
 * </p>
 * <p>
 * This action explicitly does not work on Zend Server 5.6.0 for IBMi.
 * </p>
 * 
 * Request Parameters:
 * <table border="1">
 * <tr>
 * <th>Parameter</th>
 * <th>Type</th>
 * <th>Required</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>requestUid</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>The request identifier, obtained from response cookie.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorGetRequestSummaryRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public MonitorGetRequestSummaryRequest(WebApiVersion version, Date date,
			String keyName, String userAgent, String host, String secretKey,
			ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getMethod()
	 */
	public Method getMethod() {
		return Method.GET;
	}

	/**
	 * The request identifier, obtained from response cookie.
	 * 
	 * @param uid
	 */
	public void setRequestUid(String requestUid) {
		addParameter("requestUid", requestUid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getResponseCodeList()
	 */
	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.core.connection.request.IRequest#getExpectedResponseDataType
	 * ()
	 */
	public ResponseType getExpectedResponseDataType() {
		return ResponseType.REQUEST_SUMMARY;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "monitorGetRequestSummary";
	}

}
