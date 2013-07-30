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
 * Retrieve a list of code-tracing files available for download using
 * codetracingDownloadTraceFile.
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
 * <td>applications</td>
 * <td>Array</td>
 * <td>No</td>
 * <td>List of application IDs. If specified, code-tracing entries will be
 * returned for these applications only. Default: all applications.</td>
 * </tr>
 * <tr>
 * <td>Limit</td>
 * <td>Integer</td>
 * <td>No</td>
 * <td>Row limit to retrieve, defaults to value defined in zend-user-user.ini.</td>
 * </tr>
 * <tr>
 * <td>Offset</td>
 * <td>Integer</td>
 * <td>No</td>
 * <td>The page offset to be displayed, defaults to 0.</td>
 * </tr>
 * <tr>
 * <td>orderBy</td>
 * <td>String</td>
 * <td>No</td>
 * <td>Column to sort the result by (Id, Date, Url, CreatedBy, Filesize),
 * defaults to Date</td>
 * </tr>
 * <tr>
 * <td>direction</td>
 * <td>String</td>
 * <td>No</td>
 * <td>Sorting direction, defaults to Desc.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class CodeTracingListRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public CodeTracingListRequest(WebApiVersion version, Date date,
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
	 * List of application IDs. If specified, code-tracing entries will be
	 * returned for these applications only. Default: all applications.
	 * 
	 * @param array
	 *            of applications ids
	 */
	public void setApplications(String... applications) {
		addParameter("applications", applications);
	}

	/**
	 * Row limit to retrieve, defaults to value defined in zend-user-user.ini.
	 * 
	 * @param limit
	 */
	public void setLimit(int limit) {
		addParameter("limit", limit);
	}

	/**
	 * The page offset to be displayed, defaults to 0.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		addParameter("offset", offset);
	}

	/**
	 * Column to sort the result by (Id, Date, Url, CreatedBy, Filesize),
	 * defaults to Date.
	 * 
	 * @param column
	 *            name
	 */
	public void setOrderBy(String orderBy) {
		addParameter("orderBy", orderBy);
	}

	/**
	 * Sorting direction, defaults to Desc.
	 * 
	 * @param direction
	 */
	public void setDirection(String direction) {
		addParameter("direction", direction);
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
		return ResponseType.CODE_TRACING_LIST;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "codetracingList";
	}

}
