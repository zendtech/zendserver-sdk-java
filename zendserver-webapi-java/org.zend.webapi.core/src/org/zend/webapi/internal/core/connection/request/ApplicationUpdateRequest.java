/*******************************************************************************
 * Copyright (c) Apr 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.Date;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Update/redeploy an existing application. The package provided must be of the
 * same application. Additionally any new parameters or new values to existing
 * parameters must be provided. This process is asynchronous – the initial
 * request will wait until the package is uploaded and verified, and the initial
 * response will show information about the new version being deployed –
 * however the staging and activation process will proceed after the response is
 * returned. The user is expected to continue checking the application status
 * using the applicationGetStatus method until the deployment process is
 * complete.
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
 * <td>Integer</td>
 * <td>Yes</td>
 * <td>Application ID to update.</td>
 * </tr>
 * <tr>
 * <td>appPackage</td>
 * <td>File</td>
 * <td>Yes</td>
 * <td>Application package file. Content type for the file must be
 * 'application/vnd.zend.applicationpackage'.</td>
 * </tr>
 * <tr>
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
public class ApplicationUpdateRequest extends AbstractRequest {

	public static final MediaType APPLICATION_PACKAGE = MediaType.register(
			"application/vnd.zend.applicationpackage",
			"Zend Servier Application Package");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.ACCEPTED };

	public ApplicationUpdateRequest(WebApiVersion version, Date date,
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
	 * Application package file. Content type for the file must be
	 * 'application/vnd.zend.applicationpackage'.
	 * 
	 * @param appPackage
	 */
	public void setAppPackage(NamedInputStream appPackage) {
		addParameter("appPackage", appPackage);
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
	 * Set values for user parameters defined in the package; Depending on
	 * package definitions, this parameter may be required; Each user parameter
	 * defined in the package must be provided as a key for this parameter.
	 * 
	 * @param user
	 *            params
	 */
	public void setUserParams(Map<String, String> params) {
		addParameter("userParams", params);
	}

	/**
	 * Application ID to update.
	 * 
	 * @param appId
	 */
	public void setAppId(int appId) {
		addParameter("appId", appId);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * applyParameters(org.restlet.Request)
	 */
	public void applyParameters(Request request) {
		MultipartRepresentation rep = new MultipartRepresentation(
				getParameters(), APPLICATION_PACKAGE);
		rep.setNotifier(notifier);
		request.setEntity(rep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.internal.core.connection.request.AbstractRequest#getTimeout
	 * ()
	 */
	public long getTimeout() {
		return Long.MAX_VALUE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "applicationUpdate";
	}

}
