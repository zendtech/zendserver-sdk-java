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
 * Retrieve an events list object identified by an events-group identifier. The
 * events-group identifier is retrieved from an Issue element's data. The
 * response is a list of all event elements in the group and their full details.
 * 
 * 
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
 * <td>String</td>
 * <td>Yes</td>
 * <td>Issue identifier, provided in the issue element.</td>
 * </tr>
 * <tr>
 * <td>eventsGroupId</td>
 * <td>Integer</td>
 * <td>Yes</td>
 * <td>Event group identifier, provided in the issue element.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorGetEventGroupDetailsRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public MonitorGetEventGroupDetailsRequest(WebApiVersion version, Date date,
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
		return Method.GET;
	}

	/**
	 * Issue identifier, provided in the issue element.
	 * 
	 * @param uid
	 */
	public void setIssueId(String issueId) {
		addParameter("issueId", issueId);
	}

	/**
	 * Events group identifier, provided in the issue element.
	 * 
	 * @param uid
	 */
	public void setEventGroupId(int eventGroupId) {
		switch (getVersion()) {
		case V1_3:
			addParameter("eventsGroupId", eventGroupId);
		default:
			addParameter("eventGroupId", eventGroupId);
		}
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
		return ResponseType.EVENTS_GROUP_DETAILS;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "monitorGetEventGroupDetails";
	}

}
