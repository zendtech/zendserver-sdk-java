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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.json.JSONArray;
import org.zend.sdklib.internal.utils.json.JSONException;
import org.zend.sdklib.internal.utils.json.JSONObject;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.PhpcloudContainerStatus;

/**
 * Imports devpass containers as an array of {@link IZendTarget}.
 * 
 * This operation first authenticate to the devpaas with given username/password
 * and then to list the containers
 * 
 * @author Roy, 2011
 */
public class ZendDevCloud {

	private static final String CUSTOM_HOST_PROPERTY = "org.zend.sdk.phpcloud_host";
	private static final String UPLOAD_KEY_TOKEN = "ff9e0274b11cd48d1edd7b9706394908";
	// devpass baseurl
	public static final String DEVPASS_HOST;
	public static final String INTERNAL_DEVPASS_URL;

	// user login
	private static final String USER_LOGIN = "/user/login";
	private static final String CONTAINER_LIST = "/container/list?format=json";

	// ssh settings
	public static final String KEY_TYPE = "RSA";
	
	// target properties
	public static final String TARGET_TOKEN = ZendTarget.TEMP
			+ "devcloud.token";
	public static final String TARGET_CONTAINER = "devcloud.container";
	public static final String TARGET_CONTAINER_PASSWORD = ZendTarget.ENCRYPT
			+ "devcloud.container.password";
	public static final String SSH_PRIVATE_KEY_PATH = "ssh-private-key";
	public static final String TARGET_USERNAME = "devcloud.username";
	public static final String TARGET_PASSWORD = ZendTarget.TEMP
			+ "devcloud.password";
	public static final String STORE_PASSWORD = ZendTarget.TEMP
			+ "devcloud.storePassword";

	// base url of the devpaas (for now we use an internal one)
	private final String baseUrl;
	private PublicKeyBuilder pubKeyProvider;
	
	static {
		String val = System.getProperty(CUSTOM_HOST_PROPERTY);
		if (val != null) {
			DEVPASS_HOST = val;
		} else {
			DEVPASS_HOST = "my.phpcloud.com";
		}
		INTERNAL_DEVPASS_URL = "https://" + DEVPASS_HOST;
	}

	/**
	 * Use {@link ZendDevCloud#INTERNAL_DEVPASS_URL} as basepath
	 */
	public ZendDevCloud() {
		this(INTERNAL_DEVPASS_URL);
	}

	/**
	 * @param baseUrl
	 */
	public ZendDevCloud(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void uploadPublicKey(IZendTarget target) throws SdkException {
		String token = target.getProperty(TARGET_TOKEN);
		String container = target.getProperty(TARGET_CONTAINER);
		
		String publicKey = getPublicKeyPath(target);
		
		if (token == null) {
			throw new SdkException("Token is missing.");
		}
		
		if (container == null) {
			throw new SdkException("Container name is missing.");
		}
		
		if (publicKey == null) {
			throw new SdkException("Public key is missing.");
		}
		
		uploadPublicKey(token, container, publicKey);
	}
	
	public String getPublicKeyPath(IZendTarget target) throws PublicKeyNotFoundException {
		String path = target.getProperty(SSH_PRIVATE_KEY_PATH);
		if (pubKeyProvider == null) {
			throw new PublicKeyNotFoundException("Not able to create public key.");
		}
		return pubKeyProvider.getPublicKey(path);
	}

	/**
	 * Uploads public key 
	 * @return null on success, or an error message otherwise
	 * @throws IOException 
	 * @throws SdkException 
	 * @throws JSONException 
	 */
	public void uploadPublicKey(String token, String container, String publicKey) throws SdkException {
		final boolean orginal = setFollowRedirect();
		SSLContextInitializer.instance.setDefaultSSLFactory();

		try {
			String text;
			try {
				text = doUploadKey(token, container, publicKey);
			} catch (IOException e) {
				throw new SdkException("Connection error, while uploading public key to target", e);
			}
			
			String status;
			String message;
			try {
				final JSONObject json = new JSONObject(text);
				status = (String) json.get("status");
				message = (String) json.get("message");
			} catch (JSONException ex) {
				throw new RuntimeException("Invalid JSON response from target", ex);
			}
			
			if (! "Success".equals(status)) {
				throw new SdkException(message);
			}
			
		} finally {
			HttpURLConnection.setFollowRedirects(orginal);
			SSLContextInitializer.instance.restoreDefaultSSLFactory();
		}
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
	public IZendTarget[] detectTarget(String username, String password,
			String privateKey) throws SdkException, IOException {

		final boolean orginal = setFollowRedirect();
		SSLContextInitializer.instance.setDefaultSSLFactory();

		try {
			// authenticate
			final String token = authenticate(username, password);

			// list
			String[] targets = listContainers(token);

			// info
			return createZendTargets(token, targets, privateKey);

		} finally {
			HttpURLConnection.setFollowRedirects(orginal);
			SSLContextInitializer.instance.restoreDefaultSSLFactory();
		}
	}
	
	/**
	 * @param username
	 * @param password
	 * @return the container details, no ssh private key is assigned
	 * @throws SdkException
	 * @throws IOException
	 */
	public IZendTarget[] detectTarget(String username, String password)
			throws SdkException, IOException {
		return detectTarget(username, password, null);
	}
	
	public void setContainerPassword(String containerName, String password) {
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			String container = target
					.getProperty(ZendDevCloud.TARGET_CONTAINER);
			if (containerName.equals(container)) {
				setContainerPassword(target, password);
			}
		}
	}

	public void setContainerPassword(IZendTarget target, String password) {
		if (target instanceof ZendTarget) {
			((ZendTarget) target).addProperty(TARGET_CONTAINER_PASSWORD,
					password);
			TargetsManager manager = new TargetsManager();
			manager.updateTarget(target);
		}
	}

	public String getContainerPassword(String containerName) {
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			String container = target
					.getProperty(ZendDevCloud.TARGET_CONTAINER);
			if (containerName.equals(container)) {
				return getContainerPassword(target);
			}
		}
		return null;
	}

	public String getContainerPassword(IZendTarget target) {
		return target.getProperty(TARGET_CONTAINER_PASSWORD);
	}

	/**
	 * Wake up a Phpcloud container with specified name.
	 * 
	 * @param containerName
	 * @param username phpcloud account username
	 * @param password phpcloud account password
	 * @return <code>true</code> if container is awake
	 */
	public boolean wakeUp(String containerName, String username, String password)
			throws SdkException {
		boolean orginal = setFollowRedirect();
		SSLContextInitializer.instance.setDefaultSSLFactory();
		try {
			while (true) {
				String json = getJson(authenticate(username, password),
						"/container/" + containerName + "/overview?format=json");
				PhpcloudContainerStatus status = PhpcloudContainerStatus
						.byName(getStatus(json, ""));
				if (status == null) {
					break;
				}
				if (status == PhpcloudContainerStatus.RUNNING) {
					return true;
				}
				if (status == PhpcloudContainerStatus.SLEEPING
						|| status == PhpcloudContainerStatus.PROVISIONED) {
					Thread.sleep(5000);
				}
				if (status == PhpcloudContainerStatus.FROZEN) {
					Thread.sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			return wakeUp(containerName, username, password);
		} catch (IOException e) {
			throw new SdkException(e);
		} finally {
			HttpURLConnection.setFollowRedirects(orginal);
			SSLContextInitializer.instance.restoreDefaultSSLFactory();
		}
		return false;
	}

	private IZendTarget[] createZendTargets(String token, String[] targets,
			String privateKey) throws SdkException, IOException {
		List<IZendTarget> result = new ArrayList<IZendTarget>(targets.length);
		
		IOException exception = null;
		for (String target : targets) {
			String json = null;
			try {
				json = getJson(token, target);
			} catch (IOException e) {
				// skipping the broken target
				exception = e;
				continue;
			}
			final String[] name = resolveSubKey(json, "container", "name");
			final String[] host = resolveSubKey(json, "container", "hostname");
			final String[] key = resolveSubKey(json, "container",
					"sz_api_key_name");
			final String[] secretKey = resolveSubKey(json, "container",
					"zs_api_key");
			final ZendTarget zendTarget = new ZendTarget("0", new URL(
					"https://" + host[0]), new URL("http://" + host[0]),
					key[0], secretKey[0]);

			if (privateKey != null) {
				zendTarget.addProperty(SSH_PRIVATE_KEY_PATH, privateKey);
			}
			
			if (name != null && name.length > 0) {
				zendTarget.addProperty(TARGET_CONTAINER, name[0]);
			}
			zendTarget.addProperty(TARGET_TOKEN, token);

			result.add(zendTarget);

		}
		if (result.size() == 0 && exception != null) {
			throw exception;
		}
		return (IZendTarget[]) result.toArray(new IZendTarget[result.size()]);
	}

	private String[] listContainers(final String token) throws SdkException,
			IOException {
		final String json = getJson(token, CONTAINER_LIST);
		return resolveSubKey(json, "containers", "url");
	}
	
	private String doUploadKey(final String token, String container, String pubKey) throws IOException {
		
		URL url = new URL(baseUrl + "/container/"+ container + "/key/import?format=json");
		//URL url = new URL("http://localhost/writej.php");
		
		final String content = MessageFormat.format(
				"pubKeyFile={1}&requestToken={0}",
				URLEncoder.encode(UPLOAD_KEY_TOKEN, "UTF-8"),
				URLEncoder.encode(URLEncoder.encode(pubKey, "UTF-8"), "UTF-8"));

		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Cookie", token);
		urlConn.setRequestProperty("Host", url.getHost());
		
		urlConn.setDoOutput(true);
		urlConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		DataOutputStream printout = new DataOutputStream(
				urlConn.getOutputStream());
		printout.writeBytes(content);
		printout.flush();
		printout.close();
		
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
	
	private String getJson(String sessionId, String procedure)
			throws IOException {
		String url = baseUrl + procedure;
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(baseUrl + procedure);
		method.setRequestHeader("Cookie", sessionId); //$NON-NLS-1$
		method.setRequestHeader("Pragma", "no-cache"); //$NON-NLS-1$
		int statusCode = -1;
		try {
			statusCode = client.executeMethod(method);
			if (statusCode == 200) {
				String responseContent = new String(method.getResponseBody());
				return responseContent;
			} else {
				throw new IOException("Response code Error accessing "
						+ url.toString());
			}
		} finally {
			method.releaseConnection();
		}
	}
	
	private String getStatus(String text, String name) throws IOException {
		try {
			final JSONObject json = new JSONObject(text);
			final Object object = json.get("container");
			final JSONObject container = (JSONObject) object;
			return (String) container.get("status");
		} catch (JSONException e) {
			throw new IOException("Internal Error: error parsing json "
					+ e.getMessage());
		}
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
			throws IOException {
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
			throw new IOException("Authentication error to: " + baseUrl);
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
	
	public void setPublicKeyBuilder(PublicKeyBuilder provider) {
		this.pubKeyProvider = provider;
	}

}
