/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
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
import org.zend.webapi.core.connection.data.values.IssueStatus;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Modify an Issue's status code based on an Issue's Id and a status code.
 * Response is an issue element's updated details.
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
 * <td>issueId</td>
 * <td>Integer</td>
 * <td>Yes</td>
 * <td>The issue identifier.</td>
 * </tr>
 * <tr>
 * <td>newStatus</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>The new status to set: Open | Closed | Ignored.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorChangeIssueStatusRequest extends AbstractRequest {

	public static final MediaType FORM = MediaType.register(
			"application/x-www-form-urlencoded", "Form");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public MonitorChangeIssueStatusRequest(WebApiVersion version, Date date,
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
	 * Perform a php restart as part of the action to apply the new settings,
	 * defaults to true.
	 * 
	 * @param integer value
	 */
	public void setIssueId(int issueId) {
		addParameter("issueId", issueId);
	}

	/**
	 * Perform a php restart as part of the action to apply the new settings,
	 * defaults to true.
	 * 
	 * @param IssueStatus value
	 */
	public void setNewStatus(IssueStatus status) {
		addParameter("newStatus", status.getName());
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
		return ResponseType.ISSUE;
	}

	@Override
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
		return "monitorChangeIssueStatus";
	}

}
