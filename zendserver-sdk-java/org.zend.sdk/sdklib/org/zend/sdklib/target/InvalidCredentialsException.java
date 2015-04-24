/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import org.zend.sdklib.SdkException;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class InvalidCredentialsException extends SdkException {

	private static final long serialVersionUID = -5501427303632918874L;
	
	public InvalidCredentialsException() {
		super("Invalid credentials");
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}
	
	public InvalidCredentialsException(String message, Exception e) {
		super(message, e);
	}
	
}
