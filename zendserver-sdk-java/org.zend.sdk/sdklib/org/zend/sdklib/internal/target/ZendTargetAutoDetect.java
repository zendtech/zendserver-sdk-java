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
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;

import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.target.IZendTarget;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

/**
 * Auto detect local server
 * 
 * @author Roy, 2011
 */
public class ZendTargetAutoDetect {

	private static final String USER_INI = "zend-server-user.ini";
	private static final String NEED_TO_ELEVATE = "You need root privileges to run this script!";
	private static final String MISSING_ZEND_SERVER = "Local Zend Server couldn't be found, "
			+ "please refer to http://www.zend.com/server";
	private static final String CONFIG_FILE_LINUX = "/etc/zce.rc"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_DEB = "/etc/zce.rc-deb"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_RPM = "/etc/zce.rc-rpm"; //$NON-NLS-1$
	private static final String ZCE_PREFIX = "ZCE_PREFIX";

	private String zendServerInstallLocation = null;

	/**
	 * @return returns the Local installed Zend Server, null if no local Zend
	 *         Server installed.
	 * @throws IOException
	 */
	public IZendTarget getLocalZendServer(String targetId, String key)
			throws IOException {
		IZendTarget server = null;
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			server = getLocalZendServerFromFile(targetId);
		}

		if (EnvironmentUtils.isUnderWindows()) {
			server = getLocalZendServerFromRegistry(targetId, key);
		}

		return server;
	}

	private IZendTarget getLocalZendServerFromFile(String targetId) {
		Properties props = null;
		IZendTarget server = null;

		// Try to find the zend.rc-deb file.
		try {
			FileInputStream fileStream = new FileInputStream(
					CONFIG_FILE_LINUX_DEB);
			props = new Properties();
			props.load(fileStream);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		// If not found, find the zend.rc-rpm file.
		if (props == null) {
			try {
				FileInputStream fileStream = new FileInputStream(
						CONFIG_FILE_LINUX_RPM);
				props = new Properties();
				props.load(fileStream);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}

		// if not found, find the zend.rc file.
		if (props == null) {
			try {
				FileInputStream fileStream = new FileInputStream(
						CONFIG_FILE_LINUX);
				props = new Properties();
				props.load(fileStream);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}

		if (props != null) {

			zendServerInstallLocation = props.getProperty(ZCE_PREFIX);

			// TODO resolve key/secretkey
			try {
				server = new ZendTarget(targetId, new URL("http://localhost"),
						"fake", "fake");
			} catch (MalformedURLException e) {
				// http://localhost is valid - ignore
			}
		}
		return server;
	}

	private IZendTarget getLocalZendServerFromRegistry(String targetId,
			String key) throws IOException {
		IZendTarget server = null;
		RegistryKey zendServerKey = null;
		try {
			zendServerKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE")
					.openSubKey("Zend Technologies").openSubKey("ZendServer");

			if (zendServerKey != null) {
				zendServerInstallLocation = zendServerKey
						.getStringValue("InstallLocation");

				if (zendServerInstallLocation != null) {
					// resolve key/secret key
					final String addKeyToLocalTarget = addKeyToLocalTarget(key);

					server = new ZendTarget(targetId, new URL(
							"http://localhost"), key, addKeyToLocalTarget);
				}
			}
		} catch (NoSuchKeyException e1) {
		} catch (RegistryException e1) {
		} catch (MalformedURLException e) {
			// http://localhost is valid - else ignore
		}

		return server;
	}

	public String addKeyToLocalTarget(String key) throws IOException {
		final String secretKey = getSecretKeyInLocalhost(key);
		if (null != secretKey) {
			return secretKey;
		}

		// assert permissions are elevated
		File keysFile = getApiKeysFile();
		if (keysFile == null) {
			throw new IllegalStateException(MISSING_ZEND_SERVER);
		}

		if (!keysFile.canWrite()) {
			// "Permission denied"
			throw new IOException(NEED_TO_ELEVATE);
		}

		// write zend-server-users.ini and find key
		BufferedReader ir = new BufferedReader(new FileReader(keysFile));
		final File edited = new File(keysFile.getParentFile(), USER_INI
				+ ".tmp");
		PrintStream os = new PrintStream(edited);
		final String sk = copyWithEdits(ir, os, key);
		ir.close();
		os.close();

		keysFile.renameTo(new File(keysFile.getParentFile(), USER_INI + ".bak"));
		edited.renameTo(new File(edited.getParentFile(), USER_INI));

		return sk;
	}

	public static String copyWithEdits(BufferedReader ir, PrintStream os,
			String key) throws IOException {
		String line = ir.readLine();

		final String sk = generateSecretKey();
		boolean block = false;
		while (line != null) {
			if ("[apiKeys]".equals(line)) {
				writeApiKeyBlock(key, os, sk);
				block = true;
			} else {
				os.println(line);
			}
			line = ir.readLine();
		}

		if (!block) {
			writeApiKeyBlock(key, os, sk);
		}

		return sk;

	}

	private static void writeApiKeyBlock(String key, PrintStream os,
			final String sk) {
		os.println("[apiKeys]");

		// roy:name = "roy"
		os.print(key);
		os.print(":name = \"");
		os.print(key);
		os.println("\"");

		// roy:creationTime = 1304968104
		os.print(key);
		os.print(":creationTime = ");
		os.println(new Date().getTime());

		// roy:hash = "c86ba2bc5fb62ee916031cf78..."
		os.print(key);
		os.print(":hash = \"");
		os.print(sk);
		os.println("\"");

		// roy:role = "fullAccess"
		os.print(key);
		os.println(":role = \"fullAccess\"");

		os.println();
	}

	private File getApiKeysFile() {
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			return new File(zendServerInstallLocation
					+ "/gui/application/data/zend-server-user.ini");
		} else if (EnvironmentUtils.isUnderWindows()) {
			return new File(zendServerInstallLocation
					+ "\\GUI\\application\\data\\zend-server-user.ini");
		}

		return null;
	}

	/**
	 * Returns the secret Key (hash) for the given key
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getSecretKeyInLocalhost(String key) throws IOException {
		if (zendServerInstallLocation == null) {
			throw new IllegalStateException(MISSING_ZEND_SERVER);
		}

		// assert permissions are elevated
		File keysFile = getApiKeysFile();
		if (keysFile == null) {
			throw new IllegalStateException(MISSING_ZEND_SERVER);
		}

		if (!keysFile.canRead()) {
			// "Permission denied"
			throw new IOException(NEED_TO_ELEVATE);
		}

		// read zend-server-users.ini and find key
		final BufferedReader reader = new BufferedReader(new FileReader(
				keysFile));
		Properties p = readApiKeysSection(reader);
		reader.close();
		final String hash = p.getProperty(key + ":hash");
		if (hash != null) {
			// return secretKey if possible
			return hash;
		}

		// key not found
		return null;
	}

	public static Properties readApiKeysSection(BufferedReader reader)
			throws IOException {

		Properties properties = new Properties();
		String line = reader.readLine();
		while (line != null) {
			if ("[apiKeys]".equals(line)) {
				line = reader.readLine();
				while (line != null && !line.startsWith("[")) {
					final String[] split = line.split("=");
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

	private static String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(256, random).toString(16);
	}
}
