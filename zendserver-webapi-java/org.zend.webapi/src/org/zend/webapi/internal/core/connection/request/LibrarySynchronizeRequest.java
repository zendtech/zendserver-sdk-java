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
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Request Parameters:
 * <table border="1">
 * <tr>
 * <th>Parameter</th>
 * <th>Type</th>
 * <th>Required</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>libraryVersionId</td>
 * <td>Integer</td>
 * <td>Yes</td>
 * <td>Library version Id.</td>
 * </tr>
 * <tr>
 * <td>libPackage</td>
 * <td>File</td>
 * <td>Yes</td>
 * <td>Library package file. Content type for the file must be
 * library/vnd.zend.librarypackage’.</td>
 * </tr>
 * </table>
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibrarySynchronizeRequest extends AbstractRequest {

	public static final MediaType LIBRARY_PACKAGE = MediaType.register(
			"library/vnd.zend.librarypackage", "Zend Library Package");

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	public LibrarySynchronizeRequest(WebApiVersion version, Date date,
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
	 * Library package file. Content type for the file must be
	 * 'library/vnd.zend.librarypackage'.
	 * 
	 * @param libPackage
	 */
	public void setLibPackage(NamedInputStream libPackage) {
		addParameter("libPackage", libPackage);
	}

	/**
	 * Library package file. Content type for the file must be
	 * 'library/vnd.zend.librarypackage'.
	 * 
	 * @param libraryVersionId
	 */
	public void setLibraryVersionId(int libraryVersionId) {
		addParameter("libraryVersionId", libraryVersionId);
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
		return ResponseType.LIBRARY_LIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.internal.core.connection.request.AbstractRequest#
	 * applyParameters(org.restlet.Request)
	 */
	public void applyParameters(Request request) {
		MultipartRepresentation rep = new MultipartRepresentation(
				getParameters(), LIBRARY_PACKAGE);
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
		return "librarySynchronize";
	}

}
