/*******************************************************************************
 * Copyright (c) Feb 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.auth;


/**
 * Basic implementation of the {@link WebApiCredentials} interface that allows
 * callers to pass in the key and secret access in the constructor.
 * 
 * @author Roy, 2011
 */
public class BasicCredentials implements WebApiCredentials {

	private String secretKey;
	private String keyName;

	public BasicCredentials(String keyName, String secretKey) {
		super();
		this.secretKey = secretKey;
		this.keyName = keyName;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getSecretKey() {
		return secretKey;
	}

}
