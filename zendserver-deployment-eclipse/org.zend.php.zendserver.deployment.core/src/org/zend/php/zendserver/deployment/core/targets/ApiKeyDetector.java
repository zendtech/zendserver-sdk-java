/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;

/**
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ApiKeyDetector {

	private static final String DEFAULT_KEY = "ZendStudio"; //$NON-NLS-1$

	private static final String ZENDSERVER_GUI_URL_KEY = "zendserver_default_port"; //$NON-NLS-1$

	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String SESSION_ID = "ZS6SESSID"; //$NON-NLS-1$

	private String username;
	private String password;

	private String serverUrl;

	private String name;
	private String secretKey;

	public ApiKeyDetector(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.serverUrl = getServerUrl();
	}

	public IStatus createApiKey() {
		String sessionId = login();
		if (sessionId != null) {
			try {
				String exisitngKeys = getApiKeys(sessionId);
				Map<String, String> keys = ZendTargetAutoDetect
						.parseApiKey(exisitngKeys);
				if (keys.containsKey(DEFAULT_KEY)) {
					name = DEFAULT_KEY;
					secretKey = keys.get(DEFAULT_KEY);
					return Status.OK_STATUS;
				}
				String content = createApiKey(sessionId);
				Map<String, String> values = ZendTargetAutoDetect
						.parseApiKey(content);
				if (values.containsKey(DEFAULT_KEY)) {
					name = DEFAULT_KEY;
					secretKey = values.get(DEFAULT_KEY);
				}
			} catch (SdkException e) {
				DeploymentCore.log(e);
			}
			return Status.OK_STATUS;
		}
		return new Status(
				IStatus.ERROR,
				"Target Detection", "Could not connect with a local Zend Server"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getKey() {
		return name;
	}

	public String getSecretKey() {
		return secretKey;
	}

	@SuppressWarnings("restriction")
	private String getServerUrl() {
		try {
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals("localhost")) { //$NON-NLS-1$
					String defaultPort = server.getAttribute(
							ZENDSERVER_GUI_URL_KEY, null);
					if (defaultPort != null) {
						return "http://localhost:" + defaultPort //$NON-NLS-1$
								+ "/ZendServer"; //$NON-NLS-1$
					}
				}
			}
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// do nothing and return null
		}
		return null;
	}

	private String login() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(USERNAME, username);
		params.put(PASSWORD, password);
		String url = getUrl("/Login"); //$NON-NLS-1$
		return executeLogin(url, params);
	}

	private String createApiKey(String sessionId) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(SESSION_ID, sessionId);
		params.put(NAME, DEFAULT_KEY);
		params.put(USERNAME, "admin"); //$NON-NLS-1$
		return executeAddApiKey(getUrl("/Api/apiKeysAddKey"), params, cookies); //$NON-NLS-1$
	}

	private String getApiKeys(String sessionId) {
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(SESSION_ID, sessionId);
		return executeGetApiKeys(getUrl("/Api/apiKeysGetList"), cookies); //$NON-NLS-1$
	}

	private String getUrl(String suffix) {
		return serverUrl + suffix;
	}

	private String executeLogin(String url, Map<String, String> params) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createPostRequest(url, params);
		if (method != null) {
			int statusCode = -1;
			try {
				statusCode = client.executeMethod(method);
				if (statusCode == 302) {
					Header sessionId = method.getResponseHeader("Set-Cookie"); //$NON-NLS-1$
					String value = sessionId.getValue();
					String[] segments = value.split(";"); //$NON-NLS-1$
					for (String segment : segments) {
						String[] parts = segment.split(","); //$NON-NLS-1$
						for (String part : parts) {
							if (part.trim().startsWith(SESSION_ID)) {
								String[] id = part.split("="); //$NON-NLS-1$
								if (id.length > 1) {
									return id[1].trim();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				DeploymentCore.log(e);
			} finally {
				method.releaseConnection();
			}
		}
		return null;
	}

	private String executeAddApiKey(String url, Map<String, String> params,
			Map<String, String> cookies) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createPostRequest(url, params);
		setCookies(method, cookies);
		method.setRequestHeader("X-Accept", //$NON-NLS-1$
				"application/vnd.zend.serverapi+json;version=1.3;q=1.0"); //$NON-NLS-1$
		method.setRequestHeader("X-Request", "JSON"); //$NON-NLS-1$ //$NON-NLS-2$
		if (method != null) {
			int statusCode = -1;
			try {
				statusCode = client.executeMethod(method);
				if (statusCode == 200) {
					String responseContent = new String(
							method.getResponseBody());
					return responseContent;
				} else if (statusCode == 500) {
					String val = params.remove(NAME);
					params.put(NAME, val + new Random().nextInt());
					return executeAddApiKey(url, params, cookies);
				}
			} catch (IOException e) {
				DeploymentCore.log(e);
			} finally {
				method.releaseConnection();
			}
		}
		return null;
	}

	private String executeGetApiKeys(String url, Map<String, String> cookies) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createGetRequest(url,
				new HashMap<String, String>());
		setCookies(method, cookies);
		method.setRequestHeader("X-Accept", //$NON-NLS-1$
				"application/vnd.zend.serverapi+json;version=1.3;q=1.0"); //$NON-NLS-1$
		method.setRequestHeader("X-Request", "JSON"); //$NON-NLS-1$ //$NON-NLS-2$
		if (method != null) {
			int statusCode = -1;
			try {
				statusCode = client.executeMethod(method);
				if (statusCode == 200) {
					String responseContent = new String(
							method.getResponseBody());
					return responseContent;
				}
			} catch (IOException e) {
				DeploymentCore.log(e);
			} finally {
				method.releaseConnection();
			}
		}
		return null;
	}

	private HttpMethodBase createPostRequest(String url,
			Map<String, String> params) {
		PostMethod method = new PostMethod(url);
		Set<String> keyList = params.keySet();
		for (String key : keyList) {
			method.addParameter(key, params.get(key));
		}
		return method;
	}

	private HttpMethodBase createGetRequest(String url,
			Map<String, String> params) {
		GetMethod method = new GetMethod(url);
		NameValuePair[] query = new NameValuePair[params.size()];
		Set<String> keyList = params.keySet();
		int i = 0;
		for (String key : keyList) {
			query[i++] = new NameValuePair(key, params.get(key));
		}
		method.setQueryString(query);
		return method;
	}

	private HttpMethodBase setCookies(HttpMethodBase method,
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
