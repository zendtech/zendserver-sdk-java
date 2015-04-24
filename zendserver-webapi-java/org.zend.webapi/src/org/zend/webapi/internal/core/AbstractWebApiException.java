/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core;

import org.zend.webapi.core.WebApiException;

public abstract class AbstractWebApiException extends WebApiException {

	private static final long serialVersionUID = -2689985394039090115L;
	private String message;

	public AbstractWebApiException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
