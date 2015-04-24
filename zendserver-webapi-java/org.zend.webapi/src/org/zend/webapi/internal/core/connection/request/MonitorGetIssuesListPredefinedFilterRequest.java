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
 * Retrieve a list of monitor issues according to a preset filter identifier.
 * The filter identifier is shared with the UI's predefined filters. This WebAPI
 * method may also accept ordering details and paging limits. The response is a
 * list of issue elements with their general details and event-groups
 * identifiers.
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
 * <td>filterId</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>The predefined filter's name. This parameter is case-sensitive.</td>
 * </tr>
 * <tr>
 * <td>limit</td>
 * <td>Integer</td>
 * <td>No</td>
 * <td>The number of rows to retrieve. Default lists all events up to an
 * arbitrary limit set by the system.</td>
 * </tr>
 * <tr>
 * <td>offset</td>
 * <td>Integer</td>
 * <td>No</td>
 * <td>A paging offset to begin the issues list from. Default is 0.</td>
 * </tr>
 * <tr>
 * <td>order</td>
 * <td>String</td>
 * <td>No</td>
 * <td>Column identifier for sorting the result set (id, repeats, date,
 * eventType, fullUrl, severity, status). Default is date.</td>
 * </tr>
 * <tr>
 * <td>direction</td>
 * <td>String</td>
 * <td>No</td>
 * <td>Sorting direction: Ascending or Descending. Default is Descending.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorGetIssuesListPredefinedFilterRequest extends
		AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public MonitorGetIssuesListPredefinedFilterRequest(WebApiVersion version,
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
	 * The predefined filter's name. This parameter is case-sensitive.
	 * 
	 * @param filterId
	 */
	public void setFilterId(String filterId) {
		addParameter("filterId", filterId);
	}

	/**
	 * The number of rows to retrieve. Default lists all events up to an
	 * arbitrary limit set by the system.
	 * 
	 * @param limit
	 */
	public void setLimit(int limit) {
		addParameter("limit", limit);
	}

	/**
	 * A paging offset to begin the issues list from. Default is 0.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset) {
		addParameter("offset", offset);
	}

	/**
	 * Column identifier for sorting the result set (id, repeats, date,
	 * eventType, fullUrl, severity, status). Default is date.
	 * 
	 * @param column
	 *            name
	 */
	public void setOrder(String order) {
		addParameter("order", order);
	}

	/**
	 * Sorting direction: Ascending or Descending. Default is Descending.
	 * 
	 * @param direction
	 */
	public void setDirection(String direction) {
		addParameter("direction", direction);
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
		return ResponseType.ISSUE_LIST;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		switch (getVersion()) {
		case V1_3:
			return "monitorGetIssuesByPredefinedFilter";
		default:
			return "monitorGetIssuesListPredefinedFilter";
		}
	}

}
