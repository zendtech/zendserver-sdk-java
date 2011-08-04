/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.internal.target;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.json.JSONArray;
import org.zend.sdklib.internal.utils.json.JSONException;
import org.zend.sdklib.internal.utils.json.JSONObject;
import org.zend.sdklib.target.IZendTarget;

/**
 * Imports devpass containers as an array of {@link IZendTarget}.
 * 
 * This operation first authenticate to the devpaas with given username/password
 * and then to list the containers
 * 
 * @author Roy, 2011
 */
public class ZendDevPaasDetect {

	// devpass baseurl
	private static final String INTERNAL_DEVPASS_URL = "https://projectx.zend.com";

	// user login
	private static final String USER_LOGIN = "/user/login";
	private static final String CONTAINER_LIST = "/container/list?format=json";

	// base url of the devpaas (for now we use an internal one)
	private final String baseUrl;

	/**
	 * Use {@link ZendDevPaasDetect#INTERNAL_DEVPASS_URL} as basepath
	 */
	public ZendDevPaasDetect() {
		this(INTERNAL_DEVPASS_URL);
	}

	/**
	 * @param baseUrl
	 */
	public ZendDevPaasDetect(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Detects the containers installed in Zend DevPaas given the username and
	 * password properties
	 * 
	 * @param username
	 * @param password
	 * @return the targets as captured by Zend DevPaas
	 * @throws Exception
	 */
	public IZendTarget[] detectTarget(String username, String password)
			throws SdkException, IOException {

		final boolean orginal = setFollowRedirect();
		SSLContextInitializer.instance.setDefaultSSLFactory();

		try {
			// authenticate
			final String token = authenticate(username, password);

			// list
			String[] targets = listContainers(token);

			// info
			return createZendTargets(token, targets);

		} finally {
			HttpURLConnection.setFollowRedirects(orginal);
			SSLContextInitializer.instance.restoreDefaultSSLFactory();
		}
	}

	private IZendTarget[] createZendTargets(String token, String[] targets)
			throws SdkException, IOException {
		List<IZendTarget> result = new ArrayList<IZendTarget>(targets.length);
		for (String target : targets) {
			final String json = getJson(token, target);
			final String[] host = resolveSubKey(json, "container", "hostname");
			final String[] key = resolveSubKey(json, "container",
					"sz_api_key_name");
			final String[] secretKey = resolveSubKey(json, "container",
					"zs_api_key");
			final ZendTarget zendTarget = new ZendTarget("0", new URL("https://"
					+ host[0]), key[0], secretKey[0]);
			result.add(zendTarget);

		}
		return (IZendTarget[]) result.toArray(new IZendTarget[result.size()]);
	}

	private String[] listContainers(final String token) throws SdkException,
			IOException {
		final String json = getJson(token, CONTAINER_LIST);
		return resolveSubKey(json, "containers", "url");
	}

	private String getJson(String token, String header) throws SdkException,
			IOException {
		URL url = new URL(baseUrl + header);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestProperty("Cookie", token);

		final int responseCode = urlConn.getResponseCode();
		if (responseCode != 200) {
			throw new IOException("Response code Error accessing "
					+ url.toString());
		}

		BufferedReader is = new BufferedReader(new InputStreamReader(
				urlConn.getInputStream()));

		String json = is.readLine();
		if (json == null) {
			// TODO: error
			return null;
		}
		is.close();

		return json;
	}

	private String[] resolveSubKey(String text, String key, String subkey)
			throws SdkException {
		try {
			final JSONObject json = new JSONObject(text);
			final Object object = json.get(key);
			if (object instanceof JSONArray) {
				final JSONArray array = (JSONArray) object;
				String[] result = new String[array.length()];
				for (int i = 0; i < array.length(); i++) {
					final JSONObject jsonObject = array.getJSONObject(i);
					result[i] = (String) jsonObject.get(subkey);
				}
				return result;
			} else if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) object;
				return new String[] { (String) jsonObject.getString(subkey) };
			} else {
				throw new SdkException(
						MessageFormat
								.format("Internal Error: error parsing json [path not found {0} {1}]",
										key, subkey));
			}
		} catch (JSONException e) {
			throw new SdkException("Internal Error: error parsing json "
					+ e.getMessage());
		}
	}

	private String authenticate(String username, String password)
			throws SdkException, IOException {

		URL url = new URL(baseUrl + USER_LOGIN);
		final String content = MessageFormat.format(
				"username={0}&password={1}&Submit={2}",
				URLEncoder.encode(username, "UTF-8"),
				URLEncoder.encode(password, "UTF-8"));

		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

		urlConn.setDoOutput(true);
		urlConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		DataOutputStream printout = new DataOutputStream(
				urlConn.getOutputStream());
		printout.writeBytes(content);
		printout.flush();
		printout.close();

		final int responseCode = urlConn.getResponseCode();
		if (responseCode != 302) {
			throw new SdkException("Authentication error to: " + baseUrl);
		}

		String headerName;
		String cookie = null;
		for (int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++) {
			if ("Set-Cookie".equals(headerName)) {
				cookie = urlConn.getHeaderField(i);
			}
		}

		urlConn.disconnect();

		cookie = cookie.substring(0, cookie.indexOf(";"));
		return cookie;
	}

	private boolean setFollowRedirect() {
		final boolean orginal = HttpURLConnection.getFollowRedirects();
		HttpURLConnection.setFollowRedirects(false);
		return orginal;
	}

}
