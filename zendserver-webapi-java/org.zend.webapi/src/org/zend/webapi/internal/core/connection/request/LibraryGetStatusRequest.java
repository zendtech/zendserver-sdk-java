/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
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
 * Get the list of libraries currently deployed on the server or the cluster and
 * information about each library's available versions. If library IDs are
 * specified, will return information about the specified applications; If no
 * IDs are specified, will return information about all libraries.
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
 * <td>libraries</td>
 * <td>Array</td>
 * <td>No</td>
 * <td>List of library IDs. If specified, information will be returned about
 * these applications only. If not specified, information about all applications
 * will be returned. Note that if a non-existing application ID is provided,
 * this action will not fail but instead will return no information about the
 * specific app.</td>
 * </tr>
 * <tr>
 * <td>Direction</td>
 * <td>String</td>
 * <td>No</td>
 * <td>One of ASC|DESC. Sets the ordering direction. Ordering is always by User
 * application name.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryGetStatusRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public LibraryGetStatusRequest(WebApiVersion version, Date date,
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
	 * List of library IDs. If specified, information will be returned about
	 * these applications only. If not specified, information about all
	 * applications will be returned. Note that if a non-existing application ID
	 * is provided, this action will not fail but instead will return no
	 * information about the specific app.
	 * 
	 * @param libraries
	 */
	public void setLibraries(String... libraries) {
		addParameter("libraries", libraries);
	}

	/**
	 * One of ASC|DESC. Sets the ordering direction. Ordering is always by User
	 * application name.
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
		return ResponseType.LIBRARY_LIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "libraryGetStatus";
	}

}
