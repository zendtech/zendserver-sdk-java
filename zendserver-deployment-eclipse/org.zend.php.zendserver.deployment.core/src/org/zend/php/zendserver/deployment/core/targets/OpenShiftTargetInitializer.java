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
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.sdklib.internal.target.OpenShiftTarget.Type;
import org.zend.sdklib.internal.target.SSLContextInitializer;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.Bootstrap;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

/**
 * OpenShift target initializer. It should be called for newly created OpenShift
 * targets to automatically perform Zend Server initialization.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftTargetInitializer {

	private static final String LICENSE_KEY = "licenseKey"; //$NON-NLS-1$
	private static final String ORDER_NUMBER = "orderNumber"; //$NON-NLS-1$
	private static final String ACCEPT_TERMS = "acceptTerms"; //$NON-NLS-1$
	private static final String RETYPE_PASSWORD = "retypePassword"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$
	private static final String SESSION_ID = "ZENDSERVERSESSID"; //$NON-NLS-1$

	private String name;
	private String domain;
	private String libraDomain;
	private String password;
	private String confirmPassword;
	private Type gearProfile;

	public OpenShiftTargetInitializer(String name, String domain,
			String libraDomain, String password, String confirmPassword,
			String gearProfile) {
		super();
		this.name = name;
		this.domain = domain;
		this.libraDomain = libraDomain;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.gearProfile = Type.create(gearProfile);
	}

	public IStatus initialize() {
		switch (gearProfile) {
		case zs5_6_0:
			init();
			String sessionId = acceptEula();
			if (sessionId != null) {
				if (setPassword(sessionId) == 302) {
					if (acceptLicense(sessionId) == 302) {
						return Status.OK_STATUS;
					} else
						return new Status(
								IStatus.ERROR,
								DeploymentCore.PLUGIN_ID,
								Messages.OpenShiftTargetInitializer_AcceptLicenseFailed);
				} else {
					return new Status(
							IStatus.ERROR,
							DeploymentCore.PLUGIN_ID,
							Messages.OpenShiftTargetInitializer_SettingPasswordFailed);
				}
			} else {
				return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID,
						Messages.OpenShiftTargetInitializer_InitSessionFailed);
			}
		case zs6_0_0:
		case zs6_1_0:
			WebApiCredentials credentials = new BasicCredentials("tempKey", "tempSecret");
			try {
				WebApiClient client = new WebApiClient(credentials,
						getServerUrl() + ":80", //$NON-NLS-1$
						SSLContextInitializer.instance.getRestletContext());
				client.setCustomVersion(WebApiVersion.V1_3);
				client.setServerType(ServerType.ZEND_SERVER);
				Bootstrap result = client.bootstrapSingleServer(false, password, null, null, null,
						null, null, true);
				if (result.getSuccess()) {
					return Status.OK_STATUS;
				}
			} catch (MalformedURLException e) {
				DeploymentCore.log(e);
			} catch (WebApiException e) {
				DeploymentCore.log(e);
			}
		}
		return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID,
				MessageFormat.format("{0} gear profile is unsupported.",
						gearProfile.getName()));
	}

	private void init() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			DeploymentCore.log(e);
		}
		HttpClient client = new HttpClient();
		HttpMethodBase method = createGetRequest(getServerUrl(),
				new HashMap<String, String>());
		try {
			client.executeMethod(method);
		} catch (UnknownHostException e) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ex) {
				DeploymentCore.log(ex);
			}
			init();
		} catch (IOException e) {
			DeploymentCore.log(e);
		} finally {
			method.releaseConnection();
		}
	}

	private String getServerUrl() {
		return "http://" + name + "-" + domain + "." + libraDomain; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private int acceptLicense(String sessionId) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		String url = getURL() + "License"; //$NON-NLS-1$
		params = parseLicense(executeGet(url, cookies));
		return executePost(url, params, cookies);
	}

	private int setPassword(String sessionId) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(SESSION_ID, sessionId);
		params.put(PASSWORD, password);
		params.put(RETYPE_PASSWORD, confirmPassword);
		String url = getURL() + "Password"; //$NON-NLS-1$
		return executePost(url, params, cookies);
	}

	private String acceptEula() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ACCEPT_TERMS, "1"); //$NON-NLS-1$
		String url = getURL() + "Eula"; //$NON-NLS-1$
		return executeEula(url, params);
	}

	private String getURL() {
		return "http://" + name + "-" + domain + "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ libraDomain + "/ZendServer/Registration/"; //$NON-NLS-1$
	}

	private Map<String, String> parseLicense(String responseContent) {
		Map<String, String> result = new HashMap<String, String>();
		String[] lines = responseContent.split("\n"); //$NON-NLS-1$
		for (String line : lines) {
			String input = line.trim();
			String prefix = "<input type=\"text\" name=\"orderNumber\" id=\"orderNumber\" value=\""; //$NON-NLS-1$
			if (input.startsWith(prefix)) {
				input = input.substring(prefix.length());
				int index = input.indexOf('"');
				String orderNumber = input.substring(0, index);
				result.put(ORDER_NUMBER, orderNumber);
			}
			String prefix2 = "<input type=\"text\" name=\"licenseKey\" id=\"licenseKey\" value=\""; //$NON-NLS-1$
			if (input.startsWith(prefix2)) {
				input = input.substring(prefix2.length());
				int index = input.indexOf('"');
				String licenseKey = input.substring(0, index);
				result.put(LICENSE_KEY, licenseKey);
			}
		}
		return result;
	}

	private int executePost(String url, Map<String, String> params,
			Map<String, String> cookies) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createPostRequest(url, params);
		setCookies(method, cookies);
		if (method != null) {
			try {
				return client.executeMethod(method);
			} catch (IOException e) {
				DeploymentCore.log(e);
			} finally {
				method.releaseConnection();
			}
		}
		return -1;
	}

	private String executeEula(String url, Map<String, String> params) {
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
						if (segment.startsWith(SESSION_ID)) {
							String[] parts = segment.split("="); //$NON-NLS-1$
							if (parts.length > 1) {
								return parts[1].trim();
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

	private String executeGet(String url, Map<String, String> cookies) {
		HttpClient client = new HttpClient();
		HttpMethodBase method = createGetRequest(url,
				new HashMap<String, String>());
		setCookies(method, cookies);
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
			return null;
		}
		return null;
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

	private HttpMethodBase createPostRequest(String url,
			Map<String, String> params) {
		PostMethod method = new PostMethod(url);
		Set<String> keyList = params.keySet();
		for (String key : keyList) {
			method.addParameter(key, params.get(key));
		}
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
