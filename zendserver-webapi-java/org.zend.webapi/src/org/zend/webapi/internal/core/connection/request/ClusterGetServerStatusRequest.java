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
 * Get the list of servers in the cluster and the status of each one. On a ZSCM
 * with no valid license, this operation will fail. Note that this operation
 * will cause Zend Server Cluster Manager to check the status of servers and
 * return fresh, non-cached information. This is different from the Servers List
 * tab in the GUI, which may present cached information. Users interested in
 * reducing load by caching this information should do in their own code.
 * <p>
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
 * <td>servers</td>
 * <td>Array</td>
 * <td>No</td>
 * <td>List of server IDs. If specified, status will be returned for these
 * servers only. If not specified, the status of all servers will be returned</td>
 * </tr>
 * </table>
 * 
 * @author Roy, 2011
 * 
 */
public class ClusterGetServerStatusRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public ClusterGetServerStatusRequest(WebApiVersion version, Date date,
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

	/**
	 * List of server IDs. If specified, status will be returned for these
	 * servers only. If not specified, the status of all servers will be
	 * returned
	 * 
	 * @param servers
	 */
	public void setServers(String... servers) {
		addParameter("servers", servers);
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#getResponseCodeList()
	 */
	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.request.IRequest#getExpectedResponseDataType()
	 */
	public ResponseType getExpectedResponseDataType() {
		return IResponseData.ResponseType.SERVERS_LIST;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "clusterGetServerStatus";
	}
	
}
