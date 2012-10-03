/*******************************************************************************
 * Copyright (c) Feb 2, 2011 Zend Technologies Ltd. 
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
 * Restart PHP on all servers or on specified servers in the cluster. A 202
 * response in this case does not always indicate a successful restart of all
 * servers, and the user is advised to check the server(s) status again after a
 * few seconds using the clusterGetServerStatus command. *
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
 * <td>List of server IDs to restart. If not specified, all servers in the
 * cluster will be restarted. In single Zend Server context this parameter is
 * ignored.</td>
 * </tr>
 * <tr>
 * <td>parallelRestart</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Send the restart command to all servers at the same time. Default is
 * FALSE</td>
 * </tr>
 * </table>
 * 
 * @author Roy, 2011
 * 
 */
public class RestartPhpRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.ACCEPTED };

	public RestartPhpRequest(WebApiVersion version, Date date, String keyName,
			String userAgent, String host, String secretKey, ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	public Method getMethod() {
		return Method.POST;
	}

	/**
	 * List of server IDs to restart. If not specified, all servers in the
	 * cluster will be restarted. In single Zend Server context this parameter
	 * is ignored.
	 * 
	 * @param servers
	 * @return 
	 */
	public RestartPhpRequest setServers(String... servers) {
		addParameter("servers", servers);
		return this;
	}

	/**
	 * Send the restart command to all servers at the same time. Default is
	 * FALSE
	 * 
	 * @param parallelRestart
	 */
	public void setParallelRestart(boolean parallelRestart) {
		addParameter("parallelRestart", parallelRestart);
	}

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
		return "restartPhp";
	}
	
}
