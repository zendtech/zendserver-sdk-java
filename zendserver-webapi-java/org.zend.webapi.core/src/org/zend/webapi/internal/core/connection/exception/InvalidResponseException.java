/*******************************************************************************
 * Copyright (c) Apr 5, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.exception;

import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.AbstractWebApiException;

public class InvalidResponseException extends AbstractWebApiException {

	public InvalidResponseException(Exception e) {
		super(e.getMessage());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4690274712350829914L;

	/**
	 * @return null as no response code is attached
	 */
	@Override
	public ResponseCode getResponseCode() {
		return ResponseCode.UNKNOWN;
	}

}
