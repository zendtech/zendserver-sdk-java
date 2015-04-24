/*******************************************************************************
 * Copyright (c) Jan 23, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.restlet.engine.util.DateUtils;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

/**
 * Utility class for the webAPI
 * 
 * @author Roy, 2011
 * 
 */
public class Utils {

	private static final String HASH_ALGORITHM = "HmacSHA256";
	
	public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	/**
	 * Encryption of a given text using the provided secretKey
	 * 
	 * @param text
	 * @param secretKey
	 * @return the encoded string
	 * @throws SignatureException
	 */
	public static String hashMac(String text, String secretKey)
			throws SignatureException {

		try {
			Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
			Mac mac = Mac.getInstance(sk.getAlgorithm());
			mac.init(sk);
			final byte[] hmac = mac.doFinal(text.getBytes());
			return toHexString(hmac);
		} catch (NoSuchAlgorithmException e1) {
			throw new SignatureException(
					"error building signature, no such algorithm "
							+ HASH_ALGORITHM);
		} catch (InvalidKeyException e) {
			throw new SignatureException(
					"error building signature, invalid key " + HASH_ALGORITHM);
		}
	}

	/**
	 * @param hmac
	 * @return
	 */
	private static String toHexString(final byte[] hmac) {
		StringBuilder sb = new StringBuilder(hmac.length * 2);
		for (byte b : hmac) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Get a date string out of date object
	 * 
	 * @param date
	 * @return
	 */
	public static String getFormattedDate(Date date) {
		return DateUtils.format(date, DateUtils.FORMAT_RFC_1123.get(0));
	}
		
}
