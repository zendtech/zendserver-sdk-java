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
 * Remove a server from the cluster. The removal process may be asynchronous if
 * Session Clustering is used – if this is the case, the initial operation will
 * return an HTTP 202 response. As long as the server is not fully removed,
 * further calls to remove the same server should be idempotent. On a ZSCM with
 * no valid license, this operation will fail.
 * <p>
 * 
 * Method Parameters:
 * <table border="1">
 * <tr>
 * <th>Parameter</th>
 * <th>Type</th>
 * <th>Required</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>serverId</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Server ID</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>force</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Force-remove the server, skipping graceful shutdown process. Default is
 * FALSE</td>
 * </tr>
 * </table>
 * 
 * @author Roy, 2011
 */
public class ClusterRemoveServerRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] {
			ResponseCode.OK, ResponseCode.ACCEPTED };

	public ClusterRemoveServerRequest(WebApiVersion version, Date date,
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
		return Method.POST;
	}

	/**
	 * set Server id
	 * 
	 * @param server
	 *            Id
	 */
	public ClusterRemoveServerRequest setServerId(String serverId) {
		addParameter("serverId", serverId);
		return this;
	}

	/**
	 * Force-remove the server, skipping graceful shutdown process. Default is
	 * FALSE
	 * 
	 * @param force
	 */
	public void setForce(boolean force) {
		addParameter("force", force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getValidResponseCode()
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
		return IResponseData.ResponseType.SERVER_INFO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "clusterRemoveServer";
	}

}
