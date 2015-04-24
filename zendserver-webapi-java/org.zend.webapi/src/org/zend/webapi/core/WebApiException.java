/*******************************************************************************
 * Copyright (c) Jan 25, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core;

import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;
import org.zend.webapi.internal.core.connection.exception.InternalWebApiException;

/**
 * Interface for all WebApi exceptions
 * 
 * @author Roy, 2011
 * @see SignatureException
 * @see InternalWebApiException
 */
public abstract class WebApiException extends Exception {

	/**
	 * Generated serial no.
	 */
	private static final long serialVersionUID = 6576192567061094107L;

	/**
	 * A message attached by this exception, usually provides further
	 * information about the cause of this exception
	 */
	public abstract String getMessage();
	
	/**
	 * The response code of this exception
	 * @return response code 
	 * @see ResponseCode
	 */
	public abstract ResponseCode getResponseCode();
	

}
