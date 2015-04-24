/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.exception;

import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.AbstractWebApiException;

public class NoSuchServiceMethod extends AbstractWebApiException {

	private static final long serialVersionUID = 3608945554799221795L;

	public NoSuchServiceMethod(String method) {
		super("No such method " + method);
	}

	/**
	 * @return null
	 */
	@Override
	public ResponseCode getResponseCode() {
		return null;
	}
}
