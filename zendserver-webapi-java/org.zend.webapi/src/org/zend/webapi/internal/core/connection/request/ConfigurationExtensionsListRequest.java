/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
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
 * Extensions list Web API request.
 * 
 * @author Bartlomiej Laczkowski
 */
public class ConfigurationExtensionsListRequest extends AbstractRequest {

	public ConfigurationExtensionsListRequest(WebApiVersion version, Date date, String keyName, String userAgent,
			String host, String secretKey, ServerType type) {
		super(version, date, keyName, userAgent, host, secretKey, type);
	}

	private static final ResponseCode[] RESPONSE_CODES = new ResponseCode[] { ResponseCode.OK };

	/**
	 * Adds filter parameter.
	 * 
	 * @param filter
	 *            filter parameter value
	 */
	public void setFilter(String filter) {
		addParameter("filter", filter); //$NON-NLS-1$
	}

	@Override
	public Method getMethod() {
		return Method.GET;
	}

	@Override
	public ResponseType getExpectedResponseDataType() {
		return ResponseType.CONFIGURATION_EXTENSIONS_LIST;
	}

	@Override
	protected String getRequestName() {
		return "configurationExtensionsList"; //$NON-NLS-1$
	}

	@Override
	protected ResponseCode[] getValidResponseCode() {
		return RESPONSE_CODES;
	}

}
