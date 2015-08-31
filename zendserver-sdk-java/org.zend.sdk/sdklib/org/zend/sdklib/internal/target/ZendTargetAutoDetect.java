/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.internal.utils.json.JSONArray;
import org.zend.sdklib.internal.utils.json.JSONException;
import org.zend.sdklib.internal.utils.json.JSONObject;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.data.VhostInfo;
import org.zend.webapi.core.connection.data.VhostsList;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;

/**
 * Auto detect local server
 * 
 * @author Roy, 2011
 */
public class ZendTargetAutoDetect {

	private static final String AUTHOPEN = "/usr/libexec/authopen"; //$NON-NLS-1$

	private static final String INSTALL_LOCATION = "InstallLocation"; //$NON-NLS-1$
	private static final String USER_INI = "zend-server-user.ini"; //$NON-NLS-1$
	private static final String NEED_TO_ELEVATE = "You need root privileges to run this script!"; //$NON-NLS-1$
	private static final String MISSING_ZEND_SERVER = "Local Zend Server couldn't be found. Install it first before add it as a target." //$NON-NLS-1$
			+ "\nFor more details refer to http://www.zend.com/server."; //$NON-NLS-1$

	// linux key
	private static final String CONFIG_FILE_LINUX = "/etc/zce.rc"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_DEB = "/etc/zce.rc-deb"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_RPM = "/etc/zce.rc-rpm"; //$NON-NLS-1$
	private static final String ZCE_PREFIX = "ZCE_PREFIX"; //$NON-NLS-1$
	private static final String APACHE_PORT = "APACHE_PORT"; //$NON-NLS-1$
	private static final String PRODUCT_VERSION = "PRODUCT_VERSION"; //$NON-NLS-1$

	// Registry
	private static final String NODE_64 = "WOW6432node"; //$NON-NLS-1$
	private static final String ZEND_SERVER = "ZendServer"; //$NON-NLS-1$
	private static final String APACHE_APP_PORT = "ApacheAppPort"; //$NON-NLS-1$
	private static final String IIS_APP_PORT = "IISAppProt"; //$NON-NLS-1$
	private static final String ZEND_TECHNOLOGIES = "Zend Technologies"; //$NON-NLS-1$
	private static final String VERSION = "Version"; //$NON-NLS-1$

	/**
	 * Set this to true if you agree to get GUI dialogs, such as privileges
	 * elevation dialog on MacOS. Set this to false otherwise, e.g. if you're
	 * running from command line and don't want to open any dialogs.
	 */
	public static boolean CAN_OPEN_GUI_DIALOGS = false;

	public static URL localhost = null;
	private String zendServerInstallLocation = null;

	private Process macElevatedWrite;

	static {
		try {
			localhost = new URL("http://localhost:10081"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			// ignore localhost is a valid URL
		}
	}

	public ZendTargetAutoDetect() {
		this(true);
	}

	public ZendTargetAutoDetect(boolean init) {
		if (init) {
			init();
		}
	}

	public void init() {
		if (zendServerInstallLocation == null) {
			zendServerInstallLocation = findLocalhostInstallDirectory();
		}
	}

	/**
	 * 
	 * @param targetId
	 * @param key
	 * @return the new target
	 * @throws IOException
	 */
	public IZendTarget createLocalhostTarget(String targetId, String key) throws IOException {

		// find localhost install directory
		String secretKey = findExistingSecretKey(key);
		if (secretKey == null) {
			secretKey = applySecretKey(key, generateSecretKey());
		}

		return createLocalhostTarget(targetId, key, secretKey);
	}

	/**
	 * 
	 * @param targetId
	 * @param key
	 * @return the new target
	 * @throws IOException
	 */
	public IZendTarget createLocalhostTarget(String targetId, String key, String secretKey) throws IOException {
		// create the target
		return new ZendTarget(targetId, localhost, getDefaultServerURL(key, secretKey), key, secretKey);
	}

	/**
	 * Generates a secret key and assigns locally. this key is not yet applied
	 * to the local zend server. Read
	 * {@link ZendTargetAutoDetect#applySecretKey(String, String)} to apply the
	 * generated key
	 * 
	 * @param targetId
	 * @param key
	 * @return the new target
	 */
	public IZendTarget createTemporaryLocalhost(String targetId, String key) {
		final String sk = generateSecretKey();

		return new ZendTarget(targetId, localhost, getDefaultServerURL(key, sk), key, sk, true);
	}

	/**
	 * Apply key and secret key to the installed local server
	 * 
	 * @param key
	 * @param secretKey
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public String applySecretKey(String key, String secretKey) throws IOException, FileNotFoundException {

		File keysFile = getApiKeysFile();

		// assert permissions are elevated
		if (!keysFile.canWrite() && !(EnvironmentUtils.isUnderMaxOSX() && canElevatedWriteOnMac())) {
			throw new IOException(NEED_TO_ELEVATE);
		}

		// temporary file
		final File edited = File.createTempFile(USER_INI, ".tmp"); //$NON-NLS-1$
		if (!edited.exists()) {
			edited.createNewFile();
		}

		// backup file
		BufferedReader ir = new BufferedReader(new FileReader(keysFile));
		PrintStream os = new PrintStream(edited);
		copyWithoutEdits(ir, os);
		ir.close();
		os.close();

		if (EnvironmentUtils.isUnderMaxOSX() && canElevatedWriteOnMac()) {
			os = new PrintStream(openElevatedWriteOnMac(keysFile));

		} else { // the normal way
			os = new PrintStream(keysFile);
		}

		// write zend-server-users.ini and find key
		ir = new BufferedReader(new FileReader(edited));
		secretKey = copyWithEdits(ir, os, key, secretKey);
		ir.close();
		os.close();

		if (macElevatedWrite != null) {
			try {
				macElevatedWrite.waitFor();
			} catch (InterruptedException e) {
				// ignore
			}
		}

		return trimQuotes(secretKey);
	}

	private OutputStream openElevatedWriteOnMac(File keysFile) throws IOException {
		macElevatedWrite = Runtime.getRuntime().exec(new String[] { AUTHOPEN, "-w", keysFile.toString() }); //$NON-NLS-1$

		return macElevatedWrite.getOutputStream();
	}

	private boolean canElevatedWriteOnMac() {
		return new File(AUTHOPEN).exists() && CAN_OPEN_GUI_DIALOGS;
	}

	public static String copyWithEdits(BufferedReader ir, PrintStream os, String key, String secretKey)
			throws IOException {
		String line = ir.readLine();
		boolean block = false;
		while (line != null) {
			if ("[apiKeys]".equals(line)) { //$NON-NLS-1$
				writeApiKeyBlock(key, os, secretKey);
				block = true;
			} else {
				os.println(line);
			}
			line = ir.readLine();
		}

		if (!block) {
			writeApiKeyBlock(key, os, secretKey);
		}

		return secretKey;
	}

	public static void copyWithoutEdits(BufferedReader ir, PrintStream os) throws IOException {
		String line = ir.readLine();
		while (line != null) {
			os.println(line);
			line = ir.readLine();
		}
	}

	public static Properties readApiKeysSection(BufferedReader reader) throws IOException {

		Properties properties = new Properties();
		String line = reader.readLine();
		while (line != null) {
			if ("[apiKeys]".equals(line)) { //$NON-NLS-1$
				line = reader.readLine();
				while (line != null && !line.startsWith("[")) { //$NON-NLS-1$
					final String[] split = line.split("="); //$NON-NLS-1$
					if (split != null && split.length == 2) {
						properties.put(split[0].trim(), split[1].trim());
					}
					line = reader.readLine();
				}
			}
			line = reader.readLine();
		}

		return properties;
	}

	public static Map<String, String> parseApiKey(String input) throws SdkException {
		Map<String, String> result = new HashMap<String, String>();
		try {
			JSONObject json = new JSONObject(input);
			json = ((JSONObject) json.get("responseData")); //$NON-NLS-1$
			JSONArray keys = ((JSONArray) json.get("keys")); //$NON-NLS-1$
			if (keys != null && keys.length() > 0) {
				int size = keys.length();
				for (int i = 0; i < size; i++) {
					json = (JSONObject) keys.get(i);
					String name = (String) json.get("name"); //$NON-NLS-1$
					String secretKey = (String) json.get("hash"); //$NON-NLS-1$
					result.put(name, secretKey);
				}
			}
		} catch (JSONException e) {
			throw new SdkException(e);
		}
		return result;
	}

	/**
	 * Returns a new target for the localhost target
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String findExistingSecretKey(String key) throws IOException {
		// assert permissions are elevated
		File keysFile = getApiKeysFile();
		if (!keysFile.canRead()) {
			// "Permission denied"
			throw new IOException(NEED_TO_ELEVATE);
		}

		// read zend-server-users.ini and find key
		final BufferedReader reader = new BufferedReader(new FileReader(keysFile));
		Properties p = readApiKeysSection(reader);
		reader.close();
		final String hash = p.getProperty(key + ":hash"); //$NON-NLS-1$
		if (hash != null) {
			// return secretKey if possible
			return trimQuotes(hash);
		}

		// key not found
		return null;
	}

	protected String trimQuotes(final String hash) {
		return hash.startsWith("\"") ? hash.substring(1, hash.length() - 1) : hash; //$NON-NLS-1$
	}

	/**
	 * @return returns location of the local Zend Server instance, null if no
	 *         local Zend Server installed.
	 */
	public String findLocalhostInstallDirectory() {
		String result = null;
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			result = getLocalZendServerFromFile();
		} else {
			result = getLocalZendServerFromRegistry();
		}
		if (result == null) {
			throw new IllegalStateException(MISSING_ZEND_SERVER);
		}
		return result;
	}

	private String getLocalZendServerFromFile() {
		Properties props = getLocalZendServerProperties();
		return props != null ? props.getProperty(ZCE_PREFIX) : null;
	}

	private Properties getLocalZendServerProperties() {
		Properties props = null;
		// Try to find the zend.rc-deb file.
		try {
			FileInputStream fileStream = new FileInputStream(CONFIG_FILE_LINUX_DEB);
			props = new Properties();
			props.load(fileStream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		// If not found, find the zend.rc-rpm file.
		if (props == null) {
			try {
				FileInputStream fileStream = new FileInputStream(CONFIG_FILE_LINUX_RPM);
				props = new Properties();
				props.load(fileStream);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		// if not found, find the zend.rc file.
		if (props == null) {
			try {
				FileInputStream fileStream = new FileInputStream(CONFIG_FILE_LINUX);
				props = new Properties();
				props.load(fileStream);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		return props;
	}

	private String getLocalZendServerFromRegistry() {
		try {
			RegistryKey zendServerKey = getZendServerRegistryKey();
			if (zendServerKey != null) {
				return zendServerKey.getStringValue(INSTALL_LOCATION);
			}
		} catch (RegistryException e) {
			return null;
		}
		return null;
	}

	private RegistryKey getZendServerRegistryKey() throws RegistryException {
		RegistryKey zendServerKey = null;
		try {
			zendServerKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE").openSubKey(ZEND_TECHNOLOGIES) //$NON-NLS-1$
					.openSubKey(ZEND_SERVER);
			return zendServerKey;
		} catch (NoSuchKeyException e1) {
			// try the 64 bit

			try {
				zendServerKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE").openSubKey(NODE_64) //$NON-NLS-1$
						.openSubKey(ZEND_TECHNOLOGIES).openSubKey(ZEND_SERVER);
				return zendServerKey;
			} catch (NoSuchKeyException e) {
				return null;
			}
		}
	}

	private static void writeApiKeyBlock(String key, PrintStream os, final String sk) {
		os.println("[apiKeys]"); //$NON-NLS-1$

		// roy:name = "roy"
		os.print(key);
		os.print(":name = \""); //$NON-NLS-1$
		os.print(key);
		os.println("\""); //$NON-NLS-1$

		// roy:creationTime = 1304968104
		os.print(key);
		os.print(":creationTime = "); //$NON-NLS-1$
		os.println(new Date().getTime() / 1000);

		// roy:hash = "c86ba2bc5fb62ee916031cf78..."
		os.print(key);
		os.print(":hash = \""); //$NON-NLS-1$
		os.print(sk);
		os.println("\""); //$NON-NLS-1$

		// roy:role = "fullAccess"
		os.print(key);
		os.println(":role = \"fullAccess\""); //$NON-NLS-1$

		os.println();
	}

	private File getApiKeysFile() {
		File keysFile = null;
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			keysFile = new File(zendServerInstallLocation + "/gui/application/data/zend-server-user.ini"); //$NON-NLS-1$
		} else if (EnvironmentUtils.isUnderWindows()) {
			keysFile = new File(zendServerInstallLocation + "ZendServer\\GUI\\application\\data\\zend-server-user.ini"); //$NON-NLS-1$
		}

		if (keysFile == null) {
			throw new IllegalStateException(MISSING_ZEND_SERVER);
		}
		if (!keysFile.exists()) {
			throw new IllegalArgumentException("The file " + keysFile.getAbsolutePath() + " does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return keysFile;
	}

	private static String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		final BigInteger bigInteger = new BigInteger(256, random);
		final String string = bigInteger.toString(16);
		return string.length() == 64 ? string : pad(string, 64);
	}

	/**
	 * Random number was prefixed with some zeros... pad it
	 * 
	 * @param string
	 * @param i
	 * @return
	 */
	final private static String pad(String string, int i) {
		i = i - string.length();
		StringBuilder builder = new StringBuilder(string);
		for (int j = 0; j < i; j++) {
			builder.append("0"); //$NON-NLS-1$
		}
		return builder.toString();
	}

	private URL getDefaultServerURL(String key, String secretKey) {
		String versionString = null;
		try {
			if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
				Properties props = getLocalZendServerProperties();
				if (props != null) {
					versionString = props.getProperty(PRODUCT_VERSION, null);
				}
			} else {
				// (EnvironmentUtils.isUnderWindows())
				RegistryKey zendServerKey = getZendServerRegistryKey();
				if (zendServerKey != null) {
					versionString = zendServerKey.getStringValue(VERSION);
				}
			}
			
			if(isIISBased())
				return getDefaultIISServerURL();

			ZendServerVersion version = ZendServerVersion.byName(versionString);
			if (ZendServerVersion.v6_2_x.compareTo(version) < -1)
				return getDefaultServerURLFromConfig();
			
			return getDefaultServerURLWithAPI(key, secretKey);
		} catch (Exception e) {
			// if any exception occurs ignore it and return localhost
		}
		return localhost;
	}

	private boolean isIISBased() {
		if(!EnvironmentUtils.isUnderWindows())
			return false;
		
		try {
			RegistryKey zendServerKey = getZendServerRegistryKey();
			RegistryValue portValue = zendServerKey.getValue(IIS_APP_PORT);
			return (portValue != null);
		} catch (RegistryException e) {
		}
		return false;
	}
	
	private URL getDefaultIISServerURL() throws RegistryException, MalformedURLException {
		int port = -1;
		RegistryKey zendServerKey = getZendServerRegistryKey();
		if (zendServerKey != null) {
			RegistryValue portValue = zendServerKey.getValue(IIS_APP_PORT);
			if (portValue != null) {
				port = converByteArrayToInt(portValue.getByteData());
			}
		}
		return new URL(localhost.getProtocol(), localhost.getHost(), port, ""); //$NON-NLS-1$
	}

	private URL getDefaultServerURLWithAPI(String key, String secretKey) throws MalformedURLException, WebApiException, DetectionException {
		WebApiClient apiClient = new WebApiClient(new BasicCredentials(key, secretKey), localhost.toString());
		apiClient.setServerType(ServerType.ZEND_SERVER);
		VhostsList vhostsList = apiClient.vhostGetStatus();
		for (VhostInfo	vhostInfo : vhostsList.getVhosts()) {
			if(!vhostInfo.isDefaultVhost())
				continue;
			
			return new URL(localhost.getProtocol(), localhost.getHost(), vhostInfo.getPort(), ""); //$NON-NLS-1$
		}
		throw new DetectionException("No default virtual host found"); //$NON-NLS-1$
	}

	private URL getDefaultServerURLFromConfig() throws RegistryException, MalformedURLException {
		int port = -1;
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			Properties props = getLocalZendServerProperties();
			if (props != null) {
				port = Integer.valueOf(props.getProperty(APACHE_PORT, "-1")); //$NON-NLS-1$
			}
		} else {
			// (EnvironmentUtils.isUnderWindows())
			RegistryKey zendServerKey = getZendServerRegistryKey();
			if (zendServerKey != null) {
				RegistryValue portValue = zendServerKey.getValue(APACHE_APP_PORT);
				if (portValue != null) {
					port = converByteArrayToInt(portValue.getByteData());
				}
			}
		}
		return new URL(localhost.getProtocol(), localhost.getHost(), port, ""); //$NON-NLS-1$
	}

	private int converByteArrayToInt(byte[] byteData) {
		int result = 0;
		for (int i = 0; i < byteData.length; i++) {
			result += (int) (byteData[i] & 0xFF) << (8 * (byteData.length - i - 1));
		}
		return result != 0 ? result : -1;
	}

}
