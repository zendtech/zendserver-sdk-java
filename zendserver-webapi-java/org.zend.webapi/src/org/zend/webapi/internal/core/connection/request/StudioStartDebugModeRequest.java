/*******************************************************************************
 * Copyright (c) Sep 30, 2012 Zend Technologies Ltd. 
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
import org.restlet.representation.Representation;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Start debug mode on the target server. Note that if this server is a member
 * on a cluster it will modify its own directive only and will not affect the
 * rest of the cluster. When checking the cluster management UI a notification
 * may be displayed to show that this particular server deviates from the
 * cluster blueprint of directive values, this is normal.
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
 * <td>filters</td>
 * <td>Array</td>
 * <td>Yes</td>
 * <td>Non empty array with filters.</td>
 * </tr>
 * <tr>
 * <td>options</td>
 * <td>Array</td>
 * <td>Yes</td>
 * <td>Debugging options.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class StudioStartDebugModeRequest extends AbstractRequest {

	public static final MediaType FORM = MediaType.register(
			"application/x-www-form-urlencoded", "Form");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public StudioStartDebugModeRequest(WebApiVersion version, Date date,
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
	 * Array of filters.
	 * 
	 * @param list
	 *            of filters
	 */
	public void setFilters(String... filters) {
		addParameter("filters", filters);
	}

	/**
	 * Debugging options
	 * 
	 * @param list
	 *            of debugging options
	 */
	public void setOptions(Map<String, String> options) {
		addParameter("options", options);
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
		return ResponseType.DEBUG_MODE;
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
		return "studioStartDebugMode";
	}

}
