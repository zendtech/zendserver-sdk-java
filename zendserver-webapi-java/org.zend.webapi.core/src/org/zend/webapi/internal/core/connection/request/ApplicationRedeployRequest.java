/*******************************************************************************
 * Copyright (c) Apr 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Redeploy an existing application, whether in order to fix a problem or to
 * reset an installation. This process is asynchronous – the initial request
 * will start the redeploy process and the initial response will show
 * information about the application being redeployed – however the redeployment
 * process will proceed after the response is returned. The user is expected to
 * continue checking the application status using the applicationGetStatus
 * method until the process is complete.
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
 * <td>appId</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Application ID to redeploy</td>
 * </tr>
 * <td>ignoreFailures</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Ignore failures during staging if only some servers reported failures; If
 * all servers report failures the operation will fail in any case. The default
 * value is FALSE - meaning any failure will return an error</td>
 * </tr>
 * <tr>
 * <td>userParam</td>
 * <td>Hashmap</td>
 * <td>No</td>
 * <td>Set values for user parameters defined in the package; Depending on
 * package definitions, this parameter may be required; Each user parameter
 * defined in the package must be provided as a key for this parameter</td>
 * </tr>
 * </table>
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationRedeployRequest extends AbstractRequest {
	
	public static final MediaType FORM = MediaType.register(
			"application/x-www-form-urlencoded",
			"Form");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.ACCEPTED };

	public ApplicationRedeployRequest(WebApiVersion version, Date date,
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
	 * Application ID to redeploy.
	 * 
	 * @param appId
	 */
	public void setAppId(int appId) {
		addParameter("appId", appId);
	}

	/**
	 * Ignore failures during staging if only some servers reported failures; If
	 * all servers report failures the operation will fail in any case. The
	 * default value is FALSE � meaning any failure will return an error.
	 * 
	 * @param ignoreFailures
	 */
	public void setIgnoreFailures(Boolean ignoreFailures) {
		addParameter("ignoreFailures", ignoreFailures);
	}

	/**
	 * List of server IDs. If specified, action will be done only on the subset
	 * of servers which are currently members of the cluster.
	 * 
	 * @param servers
	 */
	public void setServers(String... servers) {
		addParameter("servers", servers);
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
		return ResponseType.APPLICATION_INFO;
	}
	
	@Override
	public void applyParameters(Request request) {
		Representation rep = new MultipartRepresentation(getParameters(),
				FORM);
		request.setEntity(rep);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "applicationSynchronize";
	}
	
}
