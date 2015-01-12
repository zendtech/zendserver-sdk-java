/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
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
 * Get the list of virtual hosts currently used by the web server and
 * information about each virtual host. If vhost names are specified, will
 * return information about the specific virtual host. If no names are
 * specified, will return details about all virtual hosts.
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
 * <td>vhosts</td>
 * <td>Array</td>
 * <td>No</td>
 * <td>List of virtual host IDs. If specified, details will be returned about
 * these virtual hosts only. If not specified, details about all virtual hosts
 * will be returned.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2015
 * @since 1.6
 */
public class VhostGetStatusRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public VhostGetStatusRequest(WebApiVersion version, Date date,
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
	 * List of virtual host IDs. If specified, details will be returned about
	 * these virtual hosts only. If not specified, details about all virtual
	 * hosts will be returned.
	 * 
	 * @param vhosts
	 */
	public void setVhosts(String... vhosts) {
		addParameter("vhosts", vhosts);
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
		return ResponseType.VHOSTS_LIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "vhostGetStatus";
	}

}
