/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.service;

import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.request.IRequest;

/**
 * Request listener. It may be pre or post listener for request handling.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IRequestListener {

	/**
	 * Perform actions provided by {@link IRequestListener} implementation
	 * before or after request is handled.
	 * @param request 
	 * 
	 * @return processing result
	 */
	boolean perform(IRequest request) throws WebApiException;

	/**
	 * Return listener id. Id is used to recognize the same listeners registered
	 * be a different clients and avoid to register more than one of them.
	 * 
	 * @return listener id
	 */
	String getId();

}
