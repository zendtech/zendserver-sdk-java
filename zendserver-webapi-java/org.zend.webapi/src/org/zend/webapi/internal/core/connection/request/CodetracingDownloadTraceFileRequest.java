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
 * Download the amf file specified by codetracing identifier. This action used
 * to be named monitorDownloadAmf, however this action was completely replaced
 * by the new codetracingDownloadTraceFile action. MonitorDownloadAmf was
 * completely removed and will not be accessible in WebAPI 1.2.
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
 * <td>traceFile</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>Trace file identifier. Note that a codetracing identifier is provided as
 * part of the monitorGetRequestSummary xml response.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class CodetracingDownloadTraceFileRequest extends AbstractRequest {

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public CodetracingDownloadTraceFileRequest(WebApiVersion version,
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
	 * Trace file identifier. Note that a codetracing identifier is provided as
	 * part of the monitorGetRequestSummary xml response.
	 * 
	 * @param file
	 *            id
	 */
	public void setTraceFile(String file) {
		addParameter("traceFile", file);
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
		return ResponseType.CODE_TRACE_FILE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * getMethodName()
	 */
	protected String getRequestName() {
		return "codetracingDownloadTraceFile";
	}
	
}
