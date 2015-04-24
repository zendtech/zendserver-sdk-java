/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.response;

import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;

/**
 * Represents the response from the server
 */
public interface IResponse {
	
	/**
	 * @return the associated request
	 */
	public abstract IRequest getRequest();
	
	/**
	 * @return response code
	 */
	public abstract ResponseCode getCode();
	
	/**
	 * @return response version
	 */
	public abstract WebApiVersion getVersion();

	/**
	 * @return the response data
	 */
	public abstract IResponseData getData();
}
