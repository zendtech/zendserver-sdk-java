/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
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
 * <p>
 * Bootstrap a server for standalone usage in production or development
 * environment. This action is designed to give an automated process the option
 * to bootstrap a server with particular settings.
 * </p>
 * <p>
 * Note that once a server has been bootstrapped, it may not be added passively
 * into a cluster using clusterAddServer. It may still join a cluster using a
 * direct webapi (serverAddToCluster) or UI call. This WebAPI action is
 * explicitly accessible without a webapi key, only during the bootstrap stage.
 * </p>
 * <p>
 * Unlike the UI bootstrap process, this bootstrap action does not restart Zend
 * Server nor perform any authentication. A webapi key with administrative
 * capabilities is created as part of the bootstrap process so that you may
 * immediately continue working. It is up to the user what to do with this key
 * once bootstrap is completed.
 * </p>
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
 * <td>production</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>Bootstrap this server using the factory ‚Äúproduction‚Äù usage profile.
 * Default value: true</td>
 * </tr>
 * <tr>
 * <tr>
 * <td>adminPassword</td>
 * <td>string</td>
 * <td>Yes</td>
 * <td>The new administrator password to store for authentication</td>
 * </tr>
 * <tr>
 * <td>applicationUrl</td>
 * <td>string</td>
 * <td>No</td>
 * <td>The default application URL to use when displaying and handling deployed
 * application URLs in the UI. Default: empty</td>
 * </tr>
 * <tr>
 * <td>adminEmail</td>
 * <td>string</td>
 * <td>No</td>
 * <td>The default Email to use when sending notifications about events, audit
 * entries and other features</td>
 * </tr>
 * <tr>
 * <td>developerPassword</td>
 * <td>string</td>
 * <td>No</td>
 * <td>The new developer user password to be stored for authentication. If no
 * password is supplied, the developer user will not be created</td>
 * </tr>
 * <tr>
 * <td>orderNumber</td>
 * <td>string</td>
 * <td>No</td>
 * <td>License order number to store in the server‚Äôs configuration. This license
 * can be obtained from zend.com</td>
 * </tr>
 * <tr>
 * <td>licenseKey</td>
 * <td>string</td>
 * <td>No</td>
 * <td>License key to store in the server‚Äôs configuration. This license can be
 * obtained from zend.com</td>
 * </tr>
 * <tr>
 * <td>acceptEula</td>
 * <td>string</td>
 * <td>Yes</td>
 * <td>Must be set to true to accept ZS6‚Äôs EULA</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class BootstrapSingleServerRequest extends AbstractRequest {

	public static final MediaType FORM = MediaType.register(
			"application/x-www-form-urlencoded", "Form");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public BootstrapSingleServerRequest(WebApiVersion version, Date date,
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

	public void setProduction(Boolean production) {
		addParameter("production", production);
	}

	public void setAdminPassword(String password) {
		addParameter("adminPassword", password);
	}

	public void setApplicationUrl(String url) {
		addParameter("applicationUrl", url);
	}

	public void setAdminEmail(String email) {
		addParameter("adminEmail", email);
	}

	public void setDeveloperPassword(String password) {
		addParameter("developerPassword", password);
	}

	public void setOrderNumber(String orderNumber) {
		addParameter("orderNumber", orderNumber);
	}

	public void setLicenseKey(String licenseKey) {
		addParameter("licenseKey", licenseKey);
	}

	public void setAcceptEula(Boolean acceptEula) {
		addParameter("acceptEula", acceptEula);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getResponseCodeList()
	 */
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
		return ResponseType.BOOTSTRAP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * applyParameters(org.restlet.Request)
	 */
	public void applyParameters(Request request) {
		Representation rep = new MultipartRepresentation(getParameters(), FORM);
		request.setEntity(rep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "bootstrapSingleServer";
	}

}
