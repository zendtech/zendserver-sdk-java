/*******************************************************************************
 * Copyright (c) Jan 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.signature;

import org.zend.webapi.core.WebApiException;

/**
 * The signature interface provides a way to encode a
 * 
 * @author Roy, 2011
 */
public interface ISignature {

	public abstract String encode(String requestUri, String date)
			throws WebApiException;

}