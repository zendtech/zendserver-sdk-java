/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

	private static final String CONFIG_FILE_LINUX = "/etc/zce.rc"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_DEB = "/etc/zce.rc-deb"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_RPM = "/etc/zce.rc-rpm"; //$NON-NLS-1$

	/**
	 * @return returns the Local installed Zend Server, null if no local Zend
	 *         Server installed.
	 */
	public IZendTarget getLocalZendServer(String targetId) {
		IZendTarget server = null;
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			server = getLocalZendServerFromFile(targetId);
		}

		if (EnvironmentUtils.isUnderWindows()) {
			server = getLocalZendServerFromRegistry(targetId);
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

	private IZendTarget getLocalZendServerFromRegistry(String targetId) {
		IZendTarget server = null;
		RegistryKey zendServerKey = null;
		try {
			zendServerKey = Registry.HKEY_LOCAL_MACHINE.openSubKey("SOFTWARE")
					.openSubKey("Zend Technologies").openSubKey("ZendServer");

			if (zendServerKey != null) {
				final String zendServerInstallLocation = zendServerKey
						.getStringValue("InstallLocation");

				// TODO resolve key/secretkey
				
				if (zendServerInstallLocation != null) {
					server = new ZendTarget(targetId, new URL(
							"http://localhost"), "fake", "fake");
				}
			}
		} catch (NoSuchKeyException e1) {
		} catch (RegistryException e1) {
		} catch (MalformedURLException e) {
			// http://localhost is valid - else ignore
		}

		return server;
	}
}
