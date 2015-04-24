/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.restlet.util.Couple;

/**
 * Represents a type in request response
 * 
 * @author Roy, 2011
 * 
 * @param <T>
 */
public class RequestParameter<T> extends Couple<String, T> {

	public RequestParameter(String key, T value) {
		super(key, value);
	}

	public String getKey() {
		return getFirst();
	}

	public T getValue() {
		return getSecond();
	}

	public String getValueAsString() {
		final T second = getSecond();
		if (second instanceof Boolean) {
			return second.toString().toUpperCase();
		}
		return second.toString();
	}

	/**
	 * @return the value of the parameter as input stream
	 */
	public InputStream getValueAsStream() {
		final T second = getSecond();
		if (second == null) {
			throw new IllegalStateException("Value cannot be null.");
		}
		if (second instanceof Boolean) {
			return new ByteArrayInputStream(second.toString().toUpperCase()
					.getBytes());
		} else if (second instanceof NamedInputStream) {
			return ((NamedInputStream) second).getInputStream();
		}
		return new ByteArrayInputStream(second.toString().getBytes());
	}

	public String toString() {
		final T second = getSecond();

		// array case
		if (second instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (Object object : (Object[]) second) {
				buildArrayEntry(sb, i, object);
				i++;
			}
			return sb.toString();
		}

		// key/value case
		return getFirst() + "=" + getValueAsString();
	}

	/**
	 * @param sb
	 * @param isFirst
	 * @param object
	 */
	private void buildArrayEntry(StringBuilder sb, int i, Object object) {
		if (i != 0) {
			sb.append("&");
		}
		try {
			sb.append(URLEncoder.encode(getKey(), "UTF-8"));
			sb.append("%5B");
			sb.append(i);
			sb.append("%5D=");
			sb.append(URLEncoder.encode(object.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			sb.append("error-encoding-parameters");
		}
	}

}
