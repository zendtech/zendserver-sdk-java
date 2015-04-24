/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.dispatch;

import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.response.IResponse;

/**
 * Basic dispatch cycle for the WebApi service
 * @author roy
 *
 */
public interface IServiceDispatcher {

	public abstract IResponse dispatch(IRequest request) throws WebApiException;
	
}
