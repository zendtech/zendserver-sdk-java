/*******************************************************************************
 * Copyright (c) Apr 12, 2011 Zend Technologies Ltd. 
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
 * Deploy a new application to the server or cluster. This process is
 * asynchronous � the initial request will wait until the application is
 * uploaded and verified, and the initial response will show information about
 * the application being deployed � however the staging and activation process
 * will proceed after the response is returned. The user is expected to continue
 * checking the application status using the applicationGetStatus method until
 * the deployment process is complete.
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
 * <td>appPackage</td>
 * <td>File</td>
 * <td>Yes</td>
 * <td>Application package file. Content type for the file must be
 * 'application/vnd.zend.applicationpackage'.</td>
 * </tr>
 * <tr>
 * <td>baseUrl</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Base URL to deploy the application to. Must be an HTTP URL.</td>
 * </tr>
 * <tr>
 * <td>ignoreFailures</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Ignore failures during staging if only some servers reported failures; If
 * all servers report failures the operation will fail in any case. The default
 * value is FALSE � meaning any failure will return an error</td>
 * </tr>
 * <tr>
 * <td>userParam</td>
 * <td>Hashmap</td>
 * <td>No</td>
 * <td>Set values for user parameters defined in the package; Depending on
 * package definitions, this parameter may be required; Each user parameter
 * defined in the package must be provided as a key for this parameter</td>
 * </tr>
 * <tr>
 * <td>createVhost</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Create a virtual host based on the base URL if such a virtual host wasn't
 * already created by Zend Server. Default is FALSE</td>
 * </tr>
 * <tr>
 * <td>defaultServer</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Deploy the application on the default server; the base URL host provided
 * will be ignored and replaced with <default-server>. In case of a conjunction
 * of this parameter and createVhost, the latter will be ignored. Default is
 * FALSE</td>
 * </tr>
 * </table>
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationDeployRequest extends AbstractRequest {

	public static final MediaType APPLICATION_PACKAGE = MediaType.register(
			"application/vnd.zend.applicationpackage",
			"Zend Servier Application Package");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.ACCEPTED };

	public ApplicationDeployRequest(WebApiVersion version, Date date,
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
	 * Base URL to deploy the application to. Must be an HTTP URL.
	 * 
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		addParameter("baseUrl", baseUrl);
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
	 * Create a virtual host based on the base URL if such a virtual host wasn't
	 * already created by Zend Server. Default is FALSE.
	 * 
	 * @param createVhost
	 */
	public void setCreateVhost(Boolean createVhost) {
		addParameter("createVhost", createVhost);
	}

	/**
	 * Deploy the application on the default server; the base URL host provided
	 * will be ignored and replaced with <default-server>. In case of a
	 * conjunction of this parameter and createVhost, the latter will be
	 * ignored. Default is FALSE.
	 * 
	 * @param createVhost
	 */
	public void setDefaultServer(Boolean defaultServer) {
		addParameter("defaultServer", defaultServer);
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
	 * Free text for user defined application identifier; if not specified, the
	 * baseUrl parameter will be used.
	 * 
	 * @param userAppName
	 */
	public void setUserAppName(String userAppName) {
		addParameter("userAppName", userAppName);
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
		return "applicationDeploy";
	}

}
