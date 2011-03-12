/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.exception;

import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

public class WebApiCommunicationError extends WebApiException {

	private static final long serialVersionUID = 5338041397014185156L;

	@Override
	public String getMessage() {
		return "Communication Error (1001) - Connection refused";
	}

	@Override
	public ResponseCode getResponseCode() {
		return null;
	}

}
