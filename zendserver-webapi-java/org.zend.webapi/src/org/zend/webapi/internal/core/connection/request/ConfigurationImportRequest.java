/*******************************************************************************
 * Copyright (c) Feb 9, 2011 Zend Technologies Ltd. 
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
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Import a saved configuration snapshot into the server. Because this method is
 * expected to contain a file upload, parameters are expected to be encoded
 * using the â€�multipart/form-dataâ€™ content type.
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
 * <td>configFile</td>
 * <td>File</td>
 * <td>Yes</td>
 * <td>Configuration snapshot file to import. Content-type for the file must be
 * â€�application/vnd.zend.serverconfigâ€™</td>
 * </tr>
 * <tr>
 * <td>ignoreSystemMismatch</td>
 * <td>Boolean</td>
 * <td>No</td>
 * <td>If set to TRUE, configuration must be applied even if it was exported
 * from a different system (other major PHP version, ZS version or operating
 * system). Default is FALSE.</td>
 * </tr>
 * </table>
 * 
 * @author Roy, 2011
 */
public class ConfigurationImportRequest extends AbstractRequest {

	public static final MediaType APPLICATION_SERVER_CONFIG = MediaType
			.register("application/vnd.zend.serverconfig", "Zend Server Config");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public ConfigurationImportRequest(WebApiVersion version, Date date,
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
		return ResponseType.SERVERS_LIST;
	}

	/**
	 * If set to TRUE, configuration must be applied even if it was exported
	 * from a different system (other major PHP version, ZS version or operating
	 * system). Default is FALSE.
	 * 
	 * @param ignoreSystemMismatch
	 * @return
	 */
	public ConfigurationImportRequest setIgnoreSystemMismatch(
			boolean ignoreSystemMismatch) {
		addParameter("ignoreSystemMismatch", ignoreSystemMismatch);
		return this;
	}

	/**
	 * Configuration snapshot file to import. Content-type for the file must be
	 * â€�application/vnd.zend.serverconfigâ€™
	 * 
	 * @param configFile
	 * @return
	 */
	public ConfigurationImportRequest setConfigStream(NamedInputStream configFile) {
		addParameter("configFile", configFile);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * applyParameters(org.restlet.Request)
	 */
	public void applyParameters(Request request) {
		Representation rep = new MultipartRepresentation(getParameters(),
				APPLICATION_SERVER_CONFIG);
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
		return "configurationImport";
	}
	
}
