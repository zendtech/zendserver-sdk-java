/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.zend.sdklib.logger.Log;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Represents a target in the environment
 * 
 * @author Roy, 2011
 */
public class ZendTarget implements IZendTarget {

	public static final String ENCRYPT = "encrypt.";
	public static final String TEMP = "temp.";
	public static final String OPERATING_SYSTEM = "operatingSystem";
	public static final String SERVER_NAME = "serverName";

	private static final String EXTRA = "extra.";
	private String id;
	private URL host;
	private URL defaultServerURL;
	private String key;
	private String secretKey;
	private Properties properties;
	private boolean isTemporary;
	
	/**
	 * Mainly used for loading
	 */
	public ZendTarget() {
		// empty target
		this.properties = new Properties();
	}

	/**
	 * @param id
	 * @param host
	 * @param key
	 * @param secretKey
	 */
	public ZendTarget(String id, URL host, String key, String secretKey) {
		this(id, host, null, key, secretKey);
	}
	
	/**
	 * @param id
	 * @param host
	 * @param key
	 * @param secretKey
	 * @param temporary
	 */
	public ZendTarget(String id, URL host, String key, String secretKey,
			boolean temporary) {
		this(id, host, null, key, secretKey, temporary);
	}

	/**
	 * @param id
	 * @param host
	 * @param defaultServerURL
	 * @param key
	 * @param secretKey
	 */
	public ZendTarget(String id, URL host, URL defaultServerURL, String key,
			String secretKey) {
		super();
		this.id = id;
		this.host = host;
		this.defaultServerURL = defaultServerURL;
		this.key = key;
		this.secretKey = secretKey;
		this.properties = new Properties();

		validateTarget();
	}

	public ZendTarget(String id, URL host, URL defaultServerURL, String key,
			String secretKey, boolean temporary) {
		this(id, host, defaultServerURL, key, secretKey);
		this.isTemporary = temporary;
	}

	public boolean isTemporary() {
		return isTemporary;
	}

	/**
	 * 
	 * @return null or success, or an error message otherwise
	 */
	public String validateTarget() {
		if (id == null || host == null || secretKey == null || key == null
				|| key.length() == 0) {
			return "Target id, host, key name and secret must not be null.";
		}
		// id validation
		final char f = this.id.charAt(0);
		if (!Character.isJavaIdentifierStart(f) && !Character.isDigit(f)) {
			return "Target id must start with valid identifier: letter, number, $ or _";
		}
		for (int i = 1; i < this.id.length(); i++) {
			char c = this.id.charAt(i);
			if (!Character.isJavaIdentifierPart(c)) {
				return "Target id is invalid: " + c;
			}
		}

		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getHost()
	 */
	@Override
	public URL getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getAppHost()
	 */
	@Override
	public URL getDefaultServerURL() {
		if (defaultServerURL == null) {
			defaultServerURL = generateDefaultUrl();
		}
		return defaultServerURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getSecretKey()
	 */
	@Override
	public String getSecretKey() {
		return secretKey;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getServerName()
	 */
	public String getServerName() {
		return getProperty(SERVER_NAME);
	}

	/**
	 * Set target host
	 * 
	 * @param host
	 */
	public void setHost(URL host) {
		this.host = host;
	}

	/**
	 * Set default server URL
	 * 
	 * @param defaultServerURL
	 */
	public void setDefaultServerURL(URL defaultServerURL) {
		this.defaultServerURL = defaultServerURL;
	}

	/**
	 * Set target key
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Set target secret key
	 * 
	 * @param secretKey
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public void setServerName(String name) {
		addProperty(SERVER_NAME, name);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#getProperty(java.lang.String)
	 */
	@Override
	public synchronized String getProperty(String key) {
		String value = properties.getProperty(EXTRA + key);
		if (value != null && key.startsWith(ENCRYPT)) {
			return decrypt(value);
		}
		return value;
	}

	/**
	 * Adds an extra property to the target
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void addProperty(String key, String value) {
		if (key.startsWith(ENCRYPT)) {
			value = convertByteToHex(encrypt(value));
		}
		properties.put(EXTRA + key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#load(java.io.InputStream)
	 */
	@Override
	public synchronized void load(InputStream is) throws IOException {
		Properties properties = new Properties();
		properties.load(is);
		this.id = properties.getProperty("_id");
		String encrypted = properties.getProperty("_encrypted");
		if ("true".equals(encrypted)) {
			this.secretKey = decrypt(convertHexToByte(properties
					.getProperty("_secretKey")));
		} else {
			this.secretKey = properties.getProperty("_secretKey");
		}
		this.key = properties.getProperty("_key");
		this.host = new URL(properties.getProperty("_host"));
		String url = properties.getProperty("_defaultServerURL");
		if (url != null) {
			this.defaultServerURL = new URL(url);
		}
		final Set<String> stringPropertyNames = properties
				.stringPropertyNames();
		for (String keyName : stringPropertyNames) {
			if (keyName.startsWith(EXTRA)) {
				this.properties.put(keyName, properties.getProperty(keyName));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.IZendTarget#store(java.io.OutputStream)
	 */
	@Override
	public synchronized void store(OutputStream os) throws IOException {
		if (!isTemporary) {
			Properties properties = new Properties();
			properties.put("_id", getId());
			properties.put("_key", getKey());
			byte[] encryptedKey = encrypt(getSecretKey());
			if (encryptedKey != null) {
				properties.put("_encrypted", "true");
				properties.put("_secretKey", convertByteToHex(encryptedKey));
			} else {
				properties.put("_encrypted", "false");
				properties.put("_secretKey", getSecretKey());
			}
			properties.put("_host", getHost().toString());
			properties.put("_defaultServerURL", getDefaultServerURL()
					.toString());
			properties.putAll(removeTempProperites(this.properties));
			properties.store(os, "target properties for " + getId());
		}
	}

	@Override
	public boolean connect(WebApiVersion version, ServerType serverType)
			throws WebApiException, LicenseExpiredException {
		WebApiCredentials credentials = new BasicCredentials(getKey(),
				getSecretKey());
		try {
			String hostname = getHost().toString();
			WebApiClient client = new WebApiClient(credentials, hostname,
					SSLContextInitializer.instance.getRestletContext());
			if (version != WebApiVersion.UNKNOWN) {
				client.setCustomVersion(version);
			}
			client.setServerType(serverType);
			if (TargetsManager.isOpenShift(this)) {
				client.setServerType(ServerType.ZEND_SERVER);
			}
			final SystemInfo info = client.getSystemInfo();
			if (info.getLicenseInfo().getStatus() == LicenseInfoStatus.EXPIRED) {
				throw new LicenseExpiredException(info.getLicenseInfo()
						.getValidUntil());
			}
			addProperty("edition", info.getEdition().name());
			addProperty(OPERATING_SYSTEM, info.getOperatingSystem());
			addProperty("phpVersion", info.getPhpVersion());
			addProperty("status", info.getStatus().name());
			addProperty(SERVER_VERSION, info.getVersion().getName());
			addProperty("supportedApiVersions", info.getSupportedApiVersions()
					.toString());
		} catch (MalformedURLException e) {
			return false;
		} catch (final WebApiException e) {
			final String betterMessage = replaceWebApiMessage(e.getMessage());
			if (betterMessage == null) {
				throw e;
			} else {
				throw new WebApiException() {

					private static final long serialVersionUID = 1L;

					@Override
					public ResponseCode getResponseCode() {
						return e.getResponseCode();
					}

					@Override
					public String getMessage() {
						return betterMessage;
					}
				};
			}
		}
		return true;
	}

	@Override
	public boolean connect() throws WebApiException, LicenseExpiredException {
		return connect(WebApiVersion.UNKNOWN, ServerType.ZEND_SERVER_MANAGER);
	}
	
	@Override
	public ServerType getServerType() {
		if (TargetsManager.checkMinVersion(this, ZendServerVersion.v6_0_0)) {
			return ServerType.ZEND_SERVER;
		} else {
			if (TargetsManager.isOpenShift(this)) {
				return ServerType.ZEND_SERVER;
			}
			String system = getProperty(OPERATING_SYSTEM);
			if (system != null) {
				system = system.toLowerCase();
				if ("os400".equals(system) || "aix".equals(system)) {
					return ServerType.ZEND_SERVER;
				}
			}
		}
		return ServerType.ZEND_SERVER_MANAGER;
	}
	
	@Override
	public WebApiVersion getWebApiVersion() {
		if (TargetsManager.checkMinVersion(this, ZendServerVersion.v6_0_0)) {
			return WebApiVersion.V1_3;
		}
		return WebApiVersion.UNKNOWN;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ZendTarget) {
			IZendTarget t = (ZendTarget) obj;
			if (getId().equals(t.getId())) {
				return true;
			}
		}
		return false;
	}

	private String replaceWebApiMessage(String message) {
		if ("Zend Server Community Edition does not rely on licensing"
				.equals(message)) {
			return "Zend Server Community Edition does not support deployment";
		}
		return null;
	}

	public String[] getPropertiesKeys() {
		Set<Object> keyset = properties.keySet();
		List<String> result = new ArrayList<String>();
		for (Object o : keyset) {
			String key = (String) o;
			if (key.startsWith(EXTRA)) {
				result.add(key.substring(EXTRA.length()));
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	private String decrypt(String secretKey) {
		return decrypt(convertHexToByte(secretKey));
	}

	private String decrypt(byte[] secretKey) {
		try {
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(getSeq(), "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			return new String(c.doFinal(secretKey));
		} catch (Exception e) {
			Log.getInstance().getLogger(this.getClass().getName()).error(e);
			return null;
		}
	}

	private byte[] encrypt(String secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(getSeq(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, k);
			return cipher.doFinal(secretKey.getBytes());
		} catch (Exception e) {
			Log.getInstance().getLogger(this.getClass().getName()).error(e);
			return null;
		}
	}

	private byte[] getSeq() {
		return "[B@10f11b8=$eEew".getBytes();
	}

	private String convertByteToHex(byte[] chars) {
		String hex = new String();
		for (int i = 0; i < chars.length; i++) {
			String c = Integer.toHexString(0xFF & (int) chars[i]);
			if (c.length() == 1) {
				c = "0" + c;
			}
			hex += c;
		}
		return hex.toString();
	}

	private byte[] convertHexToByte(String hex) {
		byte[] result = new byte[hex.length() / 2];
		int j = 0;
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			result[j++] = (byte) decimal;
		}
		return result;
	}
	
	private URL generateDefaultUrl() {
		String system = getProperty(OPERATING_SYSTEM);
		if (system != null) {
			system = system.toLowerCase();
			try {
				if ("darwin".equals(system)) {
					return new URL("http", host.getHost(), 10088, "");
				}
				if ("linux".equals(system)) {
					return new URL("http", host.getHost(), 80, "");
				}
				if ("os400".equals(system) || "AIX".equals(system)) {
					String version = getProperty(IZendTarget.SERVER_VERSION);
					if (version != null && !version.startsWith("6")) { //$NON-NLS-1$
						return new URL("http", host.getHost(), 10088, "");
					} else {
						return new URL("http", host.getHost(), 10080, "");
					}
				}
			} catch (MalformedURLException e) {
	
			}
		}
		return host;
	}
	
	private Properties removeTempProperites(Properties input) {
		Properties result = new Properties();
		Set<Object> keys = input.keySet();
		for (Object key : keys) {
			if (!((String) key).startsWith(EXTRA + TEMP)) {
				String value = (String) input.get(key);
				result.put(key, value);
			}
		}
		return result;
	}

}
