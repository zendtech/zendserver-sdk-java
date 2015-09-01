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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.php.internal.debug.core.pathmapper.PathEntry.Type;
import org.eclipse.php.internal.debug.core.pathmapper.PathMapper;
import org.eclipse.php.internal.debug.core.pathmapper.PathMapper.Mapping;
import org.eclipse.php.internal.debug.core.pathmapper.PathMapperRegistry;
import org.eclipse.php.internal.debug.core.pathmapper.VirtualPath;
import org.eclipse.php.internal.server.core.Server;
import org.osgi.framework.Bundle;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.manager.MissingZendServerException;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;

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
	public static final String LOCAL_ZEND_SERVER_NAME = "Local Zend Server"; //$NON-NLS-1$
	public static final String ZENDSERVER_INSTALL_LOCATION = "InstallLocation";//$NON-NLS-1$
	public static final String ZENDSERVER_VERSION = "Version"; //$NON-NLS-1$

	// Linux and Mac OS X
	private static final String ZCE_PREFIX = "ZCE_PREFIX";//$NON-NLS-1$
	private static final String PRODUCT_VERSION = "PRODUCT_VERSION";  //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX = "/etc/zce.rc"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_DEB = "/etc/zce.rc-deb"; //$NON-NLS-1$
	private static final String CONFIG_FILE_LINUX_RPM = "/etc/zce.rc-rpm"; //$NON-NLS-1$

	// Windows
	private static final String ZEND_SERVER = "ZendServer";//$NON-NLS-1$
	private static final String ZEND_TECHNOLOGIES = "Zend Technologies";//$NON-NLS-1$
	private static final String SOFTWARE = "SOFTWARE";//$NON-NLS-1$
	private static final String NODE_64 = "WOW6432node";//$NON-NLS-1$
	private static final String VERSION = "Version"; //$NON-NLS-1$
	private static final String DOCUMENT_ROOT = "DocRoot"; //$NON-NLS-1$
	private static final String IIS_APP_PORT = "IISAppPort"; //$NON-NLS-1$

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
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server
	 * @throws DetectionException 
	 */
	public Server getLocalZendServer() throws DetectionException {
		if (isUnderLinux() || isUnderMaxOSX()) {
			return getLocalZendServerFromFile();
		}

		if (isUnderWindows()) {
			return getLocalZendServerFromRegistry();
		}
		
		String message = MessageFormat.format(Messages.ZendServerManager_UnsupportedOS_Error, Platform.getOS());
		throw new DetectionException(message);
	}

	/**
	 * Setup path mapping for a local Zend Server.
	 * 
	 * @param server
	 */
	public static void setupPathMapping(Server server) {
		String location = server.getAttribute(
				ZendServerManager.ZENDSERVER_INSTALL_LOCATION, null);
		if (location != null) {
			Bundle bundle = Platform
					.getBundle("org.zend.php.framework.resource"); //$NON-NLS-1$
			IPath workingLibPath = Platform.getStateLocation(bundle).append(
					"resources/ZendFramework-1/library"); //$NON-NLS-1$
			Mapping mapping = new Mapping();
			mapping.remotePath = new VirtualPath(new Path(location).append(
					"ZendServer/share/ZendFramework/library") //$NON-NLS-1$
					.toString());

			mapping.localPath = new VirtualPath(workingLibPath.toString());
			mapping.type = Type.EXTERNAL;
			PathMapper pathMapper = PathMapperRegistry.getByServer(server);
			pathMapper.setMapping(new Mapping[] { mapping });
			PathMapperRegistry.storeToPreferences();
		}
	}

	/**
	 * Detect local Zend Server from configuration files. This method is used
	 * for Linux and Mac OS X operating systems.
	 * 
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server
	 * @throws DetectionException 
	 */
	private Server getLocalZendServerFromFile() throws DetectionException {
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

		if (props == null) {
			String message = MessageFormat.format(Messages.ZendServerManager_ConfigurationFilesNotFound_Error, CONFIG_FILE_LINUX_DEB, CONFIG_FILE_LINUX_RPM, CONFIG_FILE_LINUX);
			throw new MissingZendServerException(message);
		}
		
		String installation = props.getProperty(ZCE_PREFIX);
		if (installation == null) {
			throw new DetectionException(Messages.ZendServerManager_InstallationLocationNotFound_Error);
		}
		if (!new File(installation).exists()) {
			String message = MessageFormat.format(Messages.ZendServerManager_InstallationLocationNotValid_Error, installation);
			throw new DetectionException(message);
		}

		Server server = new Server();
		server.setName(LOCAL_ZEND_SERVER_NAME);
		server.setAttribute(ZENDSERVER_INSTALL_LOCATION, installation);
		server.setAttribute(ZENDSERVER_VERSION, props.getProperty(PRODUCT_VERSION));
		server.setHost(LOCAL_HOST);
		server.setDocumentRoot(null);
		return server;
	}

	/**
	 * Detect local Zend Server from system registry. This method is used for
	 * Windows operating systems.
	 * 
	 * @return {@link Server} instance which represents locally installed Zend
	 *         Server
	 * @throws DetectionException 
	 */
	private Server getLocalZendServerFromRegistry() throws DetectionException  {
		try {
			RegistryKey zendServerKey = getZendServerRegistryKey();
			
			String installation = zendServerKey.getStringValue(ZENDSERVER_INSTALL_LOCATION);
			if (installation == null) {
				throw new DetectionException(Messages.ZendServerManager_InstallationLocationNotFound_Error);
			}

			if (!new File(installation).exists()) {
				String message = MessageFormat.format(Messages.ZendServerManager_InstallationLocationNotValid_Error, installation);
				throw new DetectionException(message);
			}

			Server server = new Server();
			server.setName(LOCAL_ZEND_SERVER_NAME);
			server.setAttribute(ZENDSERVER_INSTALL_LOCATION, installation);
			server.setAttribute(ZENDSERVER_VERSION, zendServerKey.getStringValue(VERSION));
			server.setHost(LOCAL_HOST);
			try {
				RegistryValue portValue = zendServerKey.getValue(IIS_APP_PORT);
				int port = ZendTargetAutoDetect.converByteArrayToInt(portValue.getByteData());
				server.setBaseURL("http://" + LOCAL_HOST + ":" + Integer.toString(port)); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (RegistryException|MalformedURLException ex) {
				//do nothing; if something fails the base url will be updated later
			}
			server.setDocumentRoot(zendServerKey.getStringValue(DOCUMENT_ROOT));
			return server;
		} catch (RegistryException ex) {
			throw new DetectionException(Messages.ZendServerManager_ErrorReadingInstallationParameters_Error, ex);
		}
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
		try {
			return Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE).openSubKey(ZEND_TECHNOLOGIES)
					.openSubKey(ZEND_SERVER);
		} catch (NoSuchKeyException e1) {
			// try the 64 bit
			return Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE).openSubKey(NODE_64).openSubKey(ZEND_TECHNOLOGIES)
					.openSubKey(ZEND_SERVER);
		}
	}

}
