/*******************************************************************************
 * Copyright (c) Apr 17, 2011 Zend Technologies Ltd. 
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
 * Reconfigure a cluster member to match the cluster's profile. On a ZSCM with
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
 * <td>doRestart</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Should the reconfigured server be restarted after the reconfigure action.
 * Default is FALSE</td>
 * </tr>
 * </table>
 * 
 * @author Wojtek, 2011
 */
public class ClusterReconfigureServerRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] {
			ResponseCode.OK, ResponseCode.ACCEPTED };

	public ClusterReconfigureServerRequest(WebApiVersion version, Date date,
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
	public ClusterReconfigureServerRequest setServerId(String serverId) {
		addParameter("serverId", serverId);
		return this;
	}

	/**
	 * Should the reconfigured server be restarted after the reconfigure action.
	 * Default is FALSE
	 * 
	 * @param doRestart
	 */
	public void setDoRestart(boolean doRestart) {
		addParameter("doRestart", doRestart);
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
		return "clusterReconfigureServer";
	}

}
