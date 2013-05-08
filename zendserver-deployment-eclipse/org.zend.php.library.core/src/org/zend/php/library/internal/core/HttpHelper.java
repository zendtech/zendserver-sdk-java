/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.internal.core;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class HttpHelper {

	public static String executeGetRequest(String url,
			Map<String, String> parameters, Map<String, String> cookies,
			int expectedCode) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createGetRequest(url, parameters);
		setCookies(method, cookies);
		if (method != null) {
			int statusCode = -1;
			try {
				statusCode = client.executeMethod(method);
				if (statusCode == expectedCode) {
					String responseContent = new String(
							method.getResponseBody());
					return responseContent;
				}
			} catch (IOException e) {
				// TODO handle it
			} finally {
				method.releaseConnection();
			}
		}
		return null;
	}

	public static HttpMethodBase createGetRequest(String url,
			Map<String, String> params) {
		GetMethod method = new GetMethod(url);
		if (params != null) {
			NameValuePair[] query = new NameValuePair[params.size()];
			Set<String> keyList = params.keySet();
			int i = 0;
			for (String key : keyList) {
				query[i++] = new NameValuePair(key, params.get(key));
			}
			method.setQueryString(query);
		}
		return method;
	}

	public static HttpMethodBase setCookies(HttpMethodBase method,
			Map<String, String> params) {
		if (params != null) {
			StringBuilder builder = new StringBuilder();
			Set<String> keyList = params.keySet();
			for (String key : keyList) {
				builder.append(key);
				builder.append("="); //$NON-NLS-1$
				builder.append(params.get(key));
				builder.append(";"); //$NON-NLS-1$
			}
			String value = builder.toString();
			if (value.length() > 0) {
				value = value.substring(0, value.length() - 1);
				method.setRequestHeader("Cookie", value); //$NON-NLS-1$
			}
		}
		return method;
	}

}
