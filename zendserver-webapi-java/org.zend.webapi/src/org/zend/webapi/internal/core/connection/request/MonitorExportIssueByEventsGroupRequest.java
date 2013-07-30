/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.Date;

import org.restlet.data.Method;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Export an archive containing all of the issue's information, event groups and
 * code tracing if available, ready for consumption by Zend Studio.
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
 * <td>eventGroupId</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>The issue event group identifier.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorExportIssueByEventsGroupRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public MonitorExportIssueByEventsGroupRequest(WebApiVersion version,
			Date date, String keyName, String userAgent, String host,
			String secretKey, ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.connection.request.IRequest#getMethod()
	 */
	public Method getMethod() {
		return Method.GET;
	}

	/**
	 * The issue event group identifier
	 * 
	 * @param - group id
	 */
	public void setEventGroupId(int eventsGroupId) {
		addParameter("eventGroupId", eventsGroupId);
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
		return ResponseType.ISSUE_FILE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "monitorExportIssueByEventsGroup";
	}

}
