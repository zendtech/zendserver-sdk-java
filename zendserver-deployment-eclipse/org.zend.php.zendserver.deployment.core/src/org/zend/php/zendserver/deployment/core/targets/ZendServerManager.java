/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.zendserver.deployment.core.DeploymentCore;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.RegDWordValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

/**
 * Utility class which is responsible for local Zend Server detection.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ZendServerManager {

	// old Zend Server attributes
	public static final String ZENDSERVER_ENABLED_KEY = "zendserver_enabled"; //$NON-NLS-1$
	public static final String ZENDSERVER_PORT_KEY = "zendserver_default_port"; //$NON-NLS-1$
	public static final String DEFAULT_URL_KEY = "zendserver_defaulturl"; //$NON-NLS-1$
	public static final String ZENDSERVER_GUI_URL_KEY = "zendserver_default_port"; //$NON-NLS-1$
	
	// TODO can be used for refreshing local settings
	public static final String ZENDSERVER_INSTALL_LOCATION = "InstallLocation";//$NON-NLS-1$

	// Linux and Mac OS X
	private static final String HTTPD_APACHE_CONFIG = "/apache2/conf/httpd.conf"; //$NON-NLS-1$
	private static final String ZCE_PREFIX = "ZCE_PREFIX";//$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX = "/etc/zce.rc"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_DEB = "/etc/zce.rc-deb"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_RPM = "/etc/zce.rc-rpm"; //$NON-NLS-1$
	private static final String APACHE_PORT2 = "APACHE_PORT"; //$NON-NLS-1$
	private static final String APACHE_DOCROOT = "APACHE_HTDOCS"; //$NON-NLS-1$

	// Windows
	private static final String IIS_PORT = "IISPort";//$NON-NLS-1$
	private static final String APACHE_PORT = "ApachePort";//$NON-NLS-1$
	private static final String APACHE_APP_PORT = "ApacheAppPort";//$NON-NLS-1$
	private static final String ZENDSERVER_DOC_ROOT = "DocRoot";//$NON-NLS-1$
	private static final String ZEND_SERVER = "ZendServer";//$NON-NLS-1$
	private static final String ZEND_TECHNOLOGIES = "Zend Technologies";//$NON-NLS-1$
	private static final String SOFTWARE = "SOFTWARE";//$NON-NLS-1$
	private static final String NODE_64 = "WOW6432node";//$NON-NLS-1$
	private static final String IIS_APP_PORT = "IISAppPort"; //$NON-NLS-1$

	private static final String LOCAL_ZEND_SERVER_NAME = "Local Zend Server"; //$NON-NLS-1$
	private static final String LOCAL_HOST = "localhost";//$NON-NLS-1$

	private static ZendServerManager instance = null;

	private ZendServerManager() {
	}

	/**
	 * @return {@link ZendServerManager} instance
	 */
	public static ZendServerManager getInstance() {
		if (instance == null) {
			instance = new ZendServerManager();
		}
		return instance;
	}

	/**
	 * @param server
	 *            {@link Server} instance which will be used as a base
	 *            configuration
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server; if not detected then {@link Server} instance passed as an
	 *         argument
	 */
	public Server getLocalZendServer(Server server) {
		if (isUnderLinux() || isUnderMaxOSX()) {
			server = getLocalZendServerFromFile(server);
		}

		if (isUnderWindows()) {
			try {
				server = getLocalZendServerFromRegistry(server);
			} catch (NoSuchKeyException e) {
				server = null;
			} catch (RegistryException e) {
				DeploymentCore.log(e);
			} catch (MalformedURLException e) {
				DeploymentCore.log(e);
			}
		}

		if (server != null
				&& (server.getName() == null || server.getName().isEmpty())) {
			server.setName(LOCAL_ZEND_SERVER_NAME);
		}
		return server;
	}

	/**
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server; if not detected then return <code>null</code>
	 */
	public Server getLocalZendServer() {
		Server server = null;
		return getLocalZendServer(server);
	}

	/**
	 * Detect local Zend Server from configuration files. This method is used
	 * for Linux and Mac OS X operating systems.
	 * 
	 * @param server
	 *            {@link Server} instance which will be used as a base
	 *            configuration
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server; if not detected then {@link Server} instance passed as an
	 *         argument
	 */
	private Server getLocalZendServerFromFile(Server server) {
		Properties props = null;

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
			String installation = props.getProperty(ZCE_PREFIX);

			if (installation == null) {
				installation = ""; //$NON-NLS-1$
			} else if (!new File(installation).exists()) {
				return null;
			}
			server.setAttribute(ZENDSERVER_INSTALL_LOCATION, installation);

			String portValue = null;
			String docRoot = null;
			BufferedReader httpdReader = null;
			try {
				httpdReader = new BufferedReader(new FileReader(new File(
						installation + HTTPD_APACHE_CONFIG)));
				String line = null;
				while ((line = httpdReader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("Listen")) { //$NON-NLS-1$
						String[] segments = line.trim().split(" "); //$NON-NLS-1$
						if (segments.length == 2) {
							portValue = segments[1];
						}
					} else if (line.startsWith("DocumentRoot")) { //$NON-NLS-1$
						String[] segments = line.trim().split(" "); //$NON-NLS-1$
						if (segments.length == 2) {
							String val = segments[1];
							if (val.startsWith("\"")) { //$NON-NLS-1$
								docRoot = val.substring(1, val.length() - 1);
							}
						}
					}
				}
			} catch (IOException e) {
			} finally {
				if (httpdReader != null) {
					try {
						httpdReader.close();
					} catch (IOException e) {
					}
				}
			}
			if (portValue == null) {
				portValue = props.getProperty(APACHE_PORT2);
			}
			if (portValue == null) {
				portValue = ""; //$NON-NLS-1$
			}
			if (docRoot == null) {
				docRoot = props.getProperty(APACHE_DOCROOT);
			}
			if (docRoot == null) {
				docRoot = ""; //$NON-NLS-1$
			}
			server.setDocumentRoot(docRoot);
			server.setHost(LOCAL_HOST);
			try {
				server.setBaseURL("http://" + LOCAL_HOST); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				// nothing to do - this is a safe creation;
			}
			server.setPort(portValue);
		}
		return server;
	}

	/**
	 * Detect local Zend Server from system registry. This method is used for
	 * Windows operating systems.
	 * 
	 * @param server
	 *            {@link Server} instance which will be used as a base
	 *            configuration
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server; if not detected then {@link Server} instance passed as an
	 *         argument
	 */
	private Server getLocalZendServerFromRegistry(Server server)
			throws NoSuchKeyException, RegistryException, MalformedURLException {
		RegistryKey zendServerKey = getZendServerRegistryKey();
		if (zendServerKey != null) {
			String docRoot = zendServerKey.getStringValue(ZENDSERVER_DOC_ROOT);
			server.setDocumentRoot(docRoot);

			String installation = zendServerKey
					.getStringValue(ZENDSERVER_INSTALL_LOCATION);
			server.setAttribute(ZENDSERVER_INSTALL_LOCATION, installation);

			RegDWordValue port = null;

			try {
				port = (RegDWordValue) zendServerKey.getValue(APACHE_APP_PORT);
			} catch (NoSuchValueException e) {
				// if is ISS, the value is not in the registry.
				// ignore this exception.
			}

			if (port == null) {
				try {
					port = (RegDWordValue) zendServerKey.getValue(IIS_APP_PORT);
				} catch (NoSuchValueException e) {
					// Not iis and apache?
					// Should not happen.
				}
			}

			String portValue = null;
			if (port != null) {
				portValue = String.valueOf(port.getData());
			}
			if (portValue == null) {
				portValue = ""; //$NON-NLS-1$
			}

			// Zend server admin url port
			RegDWordValue zsPort = null;
			try {
				zsPort = (RegDWordValue) zendServerKey.getValue(APACHE_PORT);
			} catch (NoSuchValueException e) {
				// if is ISS, the value is not in the registry.
				// ignore this exception.
			}

			if (zsPort == null) {
				zsPort = (RegDWordValue) zendServerKey.getValue(IIS_PORT);
			}

			server.setHost(LOCAL_HOST);
			server.setBaseURL("http://" + LOCAL_HOST);//$NON-NLS-1$

			server.setPort(portValue);
		}
		return server;
	}

	private boolean isUnderLinux() {
		return Platform.OS_LINUX.equals(Platform.getOS());
	}

	private boolean isUnderWindows() {
		return Platform.OS_WIN32.equals(Platform.getOS());
	}

	private boolean isUnderMaxOSX() {
		return Platform.OS_MACOSX.equals(Platform.getOS());
	}

	private RegistryKey getZendServerRegistryKey() throws RegistryException {
		RegistryKey zendServerKey = null;
		try {
			zendServerKey = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE)
					.openSubKey(ZEND_TECHNOLOGIES).openSubKey(ZEND_SERVER);
			return zendServerKey;
		} catch (NoSuchKeyException e1) {
			// try the 64 bit
			try {
				zendServerKey = Registry.HKEY_LOCAL_MACHINE
						.openSubKey(SOFTWARE).openSubKey(NODE_64)
						.openSubKey(ZEND_TECHNOLOGIES).openSubKey(ZEND_SERVER);
				return zendServerKey;
			} catch (NoSuchKeyException e) {
				return null;
			}
		}
	}

}
