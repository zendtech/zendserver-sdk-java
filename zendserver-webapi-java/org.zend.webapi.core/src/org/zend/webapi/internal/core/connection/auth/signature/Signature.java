/*******************************************************************************
 * Copyright (c) Jan 23, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.auth.signature;

import java.net.MalformedURLException;
import java.net.URL;

import org.zend.webapi.core.connection.signature.ISignature;
import org.zend.webapi.internal.core.Utils;

/**
 * Signature implementation
 * 
 * @author Roy, 2011
 * 
 */
public class Signature implements ISignature {

	private final String host;
	private final String userAgent;
	private final String secretKey;

	public Signature(String host, String userAgent, String secretKey) {
		this.host = host;
		this.userAgent = userAgent;
		this.secretKey = secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.webapi.core.signature.ISignature#generate(java.lang.String,
	 * java.util.Date)
	 */
	public String encode(String requestUri, String date)
			throws SignatureException {
		final String seperator = ":";
		StringBuilder sb = new StringBuilder();
		final URL url = getHostAsURL();
		sb.append(url.getAuthority());
		sb.append(seperator);
		sb.append(requestUri);
		sb.append(seperator);
		sb.append(userAgent);
		sb.append(seperator);
		sb.append(date);
		return Utils.hashMac(sb.toString(), secretKey);
	}

	/**
	 * @return
	 * @throws SignatureException
	 */
	private URL getHostAsURL() throws SignatureException {
		URL url;
		try {
			url = new URL(host);
			if (url.getPort() == 80) {
				url = new URL(url.getProtocol(), url.getHost(), -1,
						url.getFile());
			}
		} catch (MalformedURLException e) {
			throw new SignatureException("Error marsing host");
		}
		return url;
	}
}
