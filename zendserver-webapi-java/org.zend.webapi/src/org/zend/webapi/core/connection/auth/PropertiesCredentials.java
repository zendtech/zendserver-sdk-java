/*******************************************************************************
 * Copyright (c) Feb 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Simple implementation {@link WebApiCredentials} that reads in access keys
 * from a properties file. The key is expected to be in the "keyName" property
 * and the secret key id is expected to be in the "secretKey" property.
 * 
 * @author Roy, 2011
 * 
 */
public class PropertiesCredentials implements WebApiCredentials {

	private String name;
	private String secretKey;

	public PropertiesCredentials(InputStream stream) {
		Properties p = new Properties();
		try {
			p.load(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		this.name = (String) p.get("keyName");
		this.secretKey = (String) p.get("secretKey");

		if (this.name == null || this.secretKey == null) {
			throw new IllegalArgumentException(
					"missing entries keyName and/or secretKey in stream");
		}
	}

	public String getKeyName() {
		return name;
	}

	public String getSecretKey() {
		return secretKey;
	}

}
