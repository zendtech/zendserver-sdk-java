/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

/**
 * Utility class for detecting local Apache HTTP Server settings. It works
 * correctly with following Apache Server distributions: <h4>Windows</h4>
 * <ul>
 * <li>httpd binary (<a href="http://httpd.apache.org/download.cgi">http://httpd.apache.org/download.cgi</a>)</li>
 * <li>WAMP Server (<a href="http://www.wampserver.com">http://www.wampserver.com</a>)</li>
 * <li>Bitnami WAMPStack (<a href="https://bitnami.com/stack/wamp">https://bitnami.com/stack/wamp</a>)</li>
 * <li>XAMPP (<a href="http://www.apachefriends.org">http://www.apachefriends.org</a>)</li>
 * </ul>
 * <h4>Linux</h4>
 * <ul>
 * <li>httpd binary (<a href="http://httpd.apache.org/download.cgi">http://httpd.apache.org/download.cgi</a>)</li>
 * <li>installed through apt (Debian)</li>
 * <li></li>
 * </ul>
 * <h4>Mac OS X</h4>
 * <ul>
 * <li>httpd binary (<a href="http://httpd.apache.org/download.cgi">http://httpd.apache.org/download.cgi</a>)</li>
 * <li>Default Apache Server provided with operating system</li>
 * <li>MAMP (<a href="http://www.mamp.info">http://www.mamp.info</a>)</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class LocalApacheDetector {

	public static final String ID = "org.zend.php.server.ui.types.LocalApacheType"; //$NON-NLS-1$

	public static final String LOCATION = "apache2Location"; //$NON-NLS-1$

	private static final String LISTEN = "Listen"; //$NON-NLS-1$

	private static final String DOCUMENT_ROOT = "DocumentRoot"; //$NON-NLS-1$

	// Common
	private static final String BIN = "bin"; //$NON-NLS-1$
	private static final String CONF = "conf"; //$NON-NLS-1$
	private static final String APACHE = "apache"; //$NON-NLS-1$
	private static final String HTTPD_CONF = "httpd.conf"; //$NON-NLS-1$
	private static final String DEFAULT_HTTPD_CONF = CONF + File.separator
			+ HTTPD_CONF;

	// Linux
	private static final String DEBIAN_PORT_CONF = "/ports.conf"; //$NON-NLS-1$
	private static final String DEBIAN_DEFAULT_CONF = "/sites-enabled/000-default.conf"; //$NON-NLS-1$

	// Windows
	private static final String XAMPP_START_EXE = "xampp_start.exe"; //$NON-NLS-1$
	private static final String WAMPMANAGER_EXE = "wampmanager.exe"; //$NON-NLS-1$

	// Mac
	private static final String APACHE_2 = "apache2"; //$NON-NLS-1$
	private static final String MAMP_APP = "MAMP.app"; //$NON-NLS-1$

	private String port;
	private String documentRoot;

	private String location;

	public LocalApacheDetector(String location) {
		super();
		this.location = location;
	}

	/**
	 * Detect local Apache HTTP Server configuration in specified location.
	 * 
	 * @return <code>true</code> if configuration was detected successfully;
	 *         otherwise return <code>false</code>
	 */
	public boolean detect() {
		if (location != null) {
			File httpdConf = new File(location, DEFAULT_HTTPD_CONF);
			// firstly check a default configuration location (/conf/httpd.conf)
			if (httpdConf.exists()) {
				parseHttpdConf(httpdConf);
			} else {
				if (Platform.getOS().equals(Platform.OS_WIN32)) {
					if (checkWAMP(location)) {
						return true;
					}
					if (checkBitnamiWAMP(location)) {
						return true;
					}
					if (checkXAMPP(location)) {
						return true;
					}
				}
				if (Platform.getOS().equals(Platform.OS_LINUX)) {
					if (checkDebian(location)) {
						return true;
					}
				}
				if (Platform.getOS().equals(Platform.OS_MACOSX)) {
					if (checkMAMP(location)) {
						return true;
					}
				}
				// check if there is apache2 folder in specified location
				if (checkApache2(location)) {
					return true;
				}
				// finally check httpd.conf in specified location
				httpdConf = new File(location, HTTPD_CONF);
				if (httpdConf.exists()) {
					return parseHttpdConf(httpdConf);
				}
			}
		}
		return port != null && documentRoot != null;
	}

	/**
	 * Return value of Listen server setting.
	 * 
	 * @return detected port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Return value of DocumentRoot server setting.
	 * 
	 * @return detected document root
	 */
	public String getDocumentRoot() {
		return documentRoot;
	}

	protected void setPort(String port) {
		// check what is a format of Listen value;
		// possible values:
		// Listen 80
		// Listen 0.0.0.0:80
		// Listen [::0]:80
		int index = port.indexOf(':');
		if (index != -1) {
			port = port.substring(index + 1);
		}
		this.port = port;
	}

	protected void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
	}

	/**
	 * Try to detect local Apache Server configuration from MAMP distribution on
	 * Mac OS X. It tests following possible location passed as an argument:
	 * <ul>
	 * <li>MAMP's root directory (e.g. /Applications/MAMP)</li>
	 * <li>apache bin directory (e.g. /Applications/MAMP/bin/apache2)</li>
	 * </ul>
	 * 
	 * @param location
	 * @return <code>true</code> if document root and port were detected
	 *         correctly; otherwise return <code>false</code>
	 */
	private boolean checkMAMP(String location) {
		// check if it is MAMP's apache2 in bin (e.g.
		// /Applications/MAMP/bin/apache2)
		File mampApp = null;
		File apache2 = new File(location);
		if (APACHE_2.equals(apache2.getName())) {
			apache2 = apache2.getParentFile();
			if (apache2 != null) {
				apache2 = apache2.getParentFile();
				if (apache2 != null) {
					mampApp = new File(apache2, MAMP_APP);
				}
			}
		}
		// check if it is MAMP's root (e.g. /Applications/MAMP)
		if (mampApp == null) {
			mampApp = new File(location, MAMP_APP);
		}
		if (mampApp.exists()) {
			File httpdConf = new File(mampApp.getParent(), CONF
					+ File.separator + APACHE + File.separator + HTTPD_CONF);
			if (httpdConf.exists()) {
				return parseHttpdConf(httpdConf);
			}
		}
		return false;
	}

	private boolean checkDebian(String location) {
		// check debian configuration
		File portsConf = new File(location, DEBIAN_PORT_CONF);
		File defaultConf = new File(location, DEBIAN_DEFAULT_CONF);
		if (portsConf.exists() && defaultConf.exists()) {
			List<String> lines = readFile(portsConf);
			for (String line : lines) {
				if (line.startsWith(LISTEN)) {
					parseListen(line);
					break;
				}
			}
			lines = readFile(defaultConf);
			for (String line : lines) {
				line = line.trim();
				if (line.startsWith(DOCUMENT_ROOT)) {
					parseDocumentRoot(line);
					break;
				}
			}
		}
		return port != null && documentRoot != null;
	}

	/**
	 * Try to detect local Apache Server configuration from XAMPP distribution
	 * on Windows. It tests following possible location passed as an argument:
	 * <ul>
	 * <li>XAMPP's root directory (e.g. C:\xampp)</li>
	 * </ul>
	 * 
	 * @param location
	 * @return <code>true</code> if document root and port were detected
	 *         correctly; otherwise return <code>false</code>
	 */
	private boolean checkXAMPP(String location) {
		// Check if it is a XAMPP's root (e.g. C:\xamp)
		File xamppStart = new File(location, XAMPP_START_EXE);
		if (xamppStart.exists()) {
			File httpdConf = new File(location, APACHE + File.separator
					+ DEFAULT_HTTPD_CONF);
			if (httpdConf.exists()) {
				return parseHttpdConf(httpdConf);
			}
		}
		return false;
	}

	/**
	 * Try to detect local Apache Server configuration from WAMP distribution on
	 * Windows. It tests following possible location passed as an argument:
	 * <ul>
	 * <li>WAMP's root directory (e.g. C:\wamp)</li>
	 * <li>apache bin directory (e.g. C:\wamp\bin\apache)</li>
	 * </ul>
	 * 
	 * @param location
	 * @return <code>true</code> if document root and port were detected
	 *         correctly; otherwise return <code>false</code>
	 */
	private boolean checkWAMP(String location) {
		// check if it is a WAMP's root (e.g. C:\wamp)
		File wampManager = new File(location, WAMPMANAGER_EXE);
		if (wampManager.exists()) {
			return parseWAMP(location);
		}
		// Check if it apache's root (e.g. C:\wamp\bin\apache)
		File apacheFile = new File(location);
		if (APACHE.equals(apacheFile.getName())) {
			wampManager = new File(apacheFile.getParentFile().getParent(),
					WAMPMANAGER_EXE);
			if (wampManager.exists()) {
				return parseWAMP(wampManager.getParent());
			}
		}
		return false;
	}
	
	/**
	 * Try to detect local Apache Server configuration from Bitnami WAMPStack distribution on
	 * Windows. It tests following possible location passed as an argument:
	 * <ul>
	 * <li>Bitnami WAMP's root folder (e.g. C:\Bitnami\wampstack-5.4.33-0)</li>
	 * </ul>
	 * 
	 * @param location
	 * @return <code>true</code> if document root and port were detected
	 *         correctly; otherwise return <code>false</code>
	 */
	private boolean checkBitnamiWAMP(String location) {
		return checkApache2(location);
	}
	
	/**
	 * Check if there is apache2 folder in specified location.
	 * 
	 * @param location
	 * @return <code>true</code> if document root and port were detected
	 *         correctly; otherwise return <code>false</code>
	 */
	private boolean checkApache2(String location) {
		File httpdConf = new File(location, APACHE_2 + File.separator + CONF
				+ File.separator + HTTPD_CONF);
		if (httpdConf.exists()) {
			return parseHttpdConf(httpdConf);
		}
		return false;
	}

	private boolean parseWAMP(String location) {
		File apacheRoot = new File(location, BIN + File.separator + APACHE);
		if (apacheRoot.exists()) {
			String[] apacheFolders = apacheRoot.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(APACHE);
				}
			});
			apacheRoot = new File(apacheRoot,
					apacheFolders[apacheFolders.length - 1]);
			File httpdConf = new File(apacheRoot, DEFAULT_HTTPD_CONF);
			return httpdConf.exists() && parseHttpdConf(httpdConf);
		}
		return false;
	}

	private List<String> readFile(File file) {
		BufferedReader httpdReader = null;
		List<String> result = new ArrayList<String>();
		try {
			httpdReader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = httpdReader.readLine()) != null) {
				result.add(line);
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
		return result;
	}

	private void parseDocumentRoot(String line) {
		String path = extractValue(line, DOCUMENT_ROOT);
		if (path != null) {
			if (path.startsWith("\"")) { //$NON-NLS-1$
				path = path.substring(1, path.length() - 1);
			}
			setDocumentRoot(path);
		}
	}

	/**
	 * Parse Listen and DocumentRoot from specified httpd.conf file.
	 * 
	 * @param httpdConfFile
	 *            httpd.conf file
	 * @return <code>true</code> file port and document root are correctly
	 *         parsed; otherwise return <code>false</code>
	 */
	private boolean parseHttpdConf(File httpdConfFile) {
		List<String> lines = readFile(httpdConfFile);
		for (String line : lines) {
			if (port == null && line.startsWith(LISTEN)) {
				parseListen(line);
			} else if (documentRoot == null && line.startsWith(DOCUMENT_ROOT)) {
				parseDocumentRoot(line);
			}
		}
		return port != null && documentRoot != null;
	}

	private void parseListen(String line) {
		String value = extractValue(line, LISTEN);
		if (value != null) {
			setPort(value);
		}
	}

	private String extractValue(String line, String attributeName) {
		String path = line.trim().substring(attributeName.length());
		return path.trim();
	}

}
