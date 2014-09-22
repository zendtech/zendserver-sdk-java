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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for detecting local Apache HTTP Server settings.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class LocalApacheDetector {

	private static final String HTTPD_CONF = "httpd.conf"; //$NON-NLS-1$

	private static final String LISTEN = "Listen"; //$NON-NLS-1$

	private static final String DOCUMENT_ROOT = "DocumentRoot"; //$NON-NLS-1$

	public static final String ID = "org.zend.php.server.ui.types.LocalApacheType"; //$NON-NLS-1$

	public static final String LOCATION = "apache2Location"; //$NON-NLS-1$

	private static final String DEFAULT_HTTPD_CONF = "/conf/" + HTTPD_CONF; //$NON-NLS-1$

	private static final String DEBIAN_PORT_CONF = "/ports.conf"; //$NON-NLS-1$

	private static final String DEBIAN_DEFAULT_CONF = "/sites-enabled/000-default.conf"; //$NON-NLS-1$

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
			File defaultHttpdConf = new File(location, DEFAULT_HTTPD_CONF);
			if (defaultHttpdConf.exists()) {
				parseHttpdConf(defaultHttpdConf);
			} else {
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
				} else {
					// check httpd.conf in specified location
					File httpdConf = new File(location, HTTPD_CONF);
					if (httpdConf.exists()) {
						parseHttpdConf(httpdConf);
					}
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
		this.port = port;
	}

	protected void setDocumentRoot(String documentRoot) {
		this.documentRoot = documentRoot;
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
	 */
	private void parseHttpdConf(File httpdConfFile) {
		List<String> lines = readFile(httpdConfFile);
		for (String line : lines) {
			if (line.startsWith(LISTEN)) {
				parseListen(line);
			} else if (line.startsWith(DOCUMENT_ROOT)) {
				parseDocumentRoot(line);
			}
		}
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
