/*******************************************************************************
 * Copyright (c) Jan 23, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.auth.signature;

import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Exception for request signature creation
 * 
 * @author Roy, 2011
 * 
 */
public class SignatureException extends WebApiException {

	private static final long serialVersionUID = -9013546129984971695L;
	public final String error;

	public SignatureException(String error) {
		this.error = error;
	}

	@Override
	public String getMessage() {
		return error;
	}

	/**
	 * returns null as no response code is bound
	 */
	@Override
	public ResponseCode getResponseCode() {
		return null;
	}
}
