/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.response;

import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.internal.core.connection.response.Response;

/**
 * Response class factory.
 * Can be further used if required to adjust response object generation
 * 
 * @author Roy, 2011
 * 
 */
public class ResponseFactory {

	public static final IResponse createResponse(IRequest request,
			int responseCode, IResponseData responseData) {

		return new Response(request, responseCode, responseData);

	}

}
