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
 * Add a new server to the cluster. On a ZSCM with no valid license, this
 * operation will fail.
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
 * <td>serverName</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Server Name</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>serverUrl</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Server address, as a full HTTP / HTTPS URL</td>
 * </tr>
 * <tr>
 * <td>guiPassword</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Server GUI password</td>
 * </tr>
 * <tr>
 * <td>propagateSettings</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Propagate this server’s current settings to the rest of the cluster.
 * Default is FALSE</td>
 * </tr>
 * <tr>
 * <td>doRestart</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Initiate a PHP restart on the cluster after adding the server; Default is
 * FALSE. <i>Deprecated as of 1.1 in order to support automatic deployment. The
 * system will restart the added server automatically and will ignore this
 * parameter if passed.</i></td>
 * </tr>
 * </table>
 * 
 * @author Roy, 2011
 */
public class ClusterAddServerRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] {
			ResponseCode.OK, ResponseCode.ACCEPTED };

	public ClusterAddServerRequest(WebApiVersion version, Date date,
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
	 * set Server Name
	 * 
	 * @param serverName
	 */
	public ClusterAddServerRequest setServerName(String serverName) {
		addParameter("serverName", serverName);
		return this;
	}

	/**
	 * set Server address, as a full HTTP / HTTPS URL
	 * 
	 * @param serverName
	 */
	public ClusterAddServerRequest setServerUrl(String serverUrl) {
		addParameter("serverUrl", serverUrl);
		return this;
	}

	/**
	 * set Server GUI password
	 * 
	 * @param serverName
	 */
	public ClusterAddServerRequest setGuiPassword(String guiPassword) {
		addParameter("guiPassword", guiPassword);
		return this;
	}

	/**
	 * set propagateSettings - Propagate this server’s current settings to the
	 * rest of the cluster. Default is FALSE
	 * 
	 * @param serverName
	 */
	public ClusterAddServerRequest setPropagateSettings(boolean propagateSettings) {
		addParameter("propagateSettings", propagateSettings);
		return this;
	}

	/**
	 * set doRestart Initiate a PHP restart on the cluster after adding the
	 * server; Default is FALSE
	 * 
	 * @param serverName
	 * 
	 * @deprecated Deprecated as of 1.1 in order to support automatic
	 *             deployment. The system will restart the added server
	 *             automatically and will ignore this parameter if passed.
	 */
	public ClusterAddServerRequest setDoStart(boolean doRestart) {
		addParameter("doRestart", doRestart);
		return this;
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

	/* (non-Javadoc)
	 * @see org.zend.webapi.core.connection.request.IRequest#getExpectedResponseDataType()
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
		return "clusterAddServer";
	}
	
}
