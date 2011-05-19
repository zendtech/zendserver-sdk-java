/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class EnvironmentUtils {

	enum OS {
		WINDOWS, LINUX, MAC, UNKNOWN;
	}

	public static OS getOsName() {
		OS os = OS.UNKNOWN;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			os = OS.WINDOWS;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			os = OS.LINUX;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			os = OS.MAC;
		}

		return os;
	}

	public static boolean isUnderLinux() {
		return EnvironmentUtils.getOsName() == OS.LINUX;
	}

	public static boolean isUnderWindows() {
		return EnvironmentUtils.getOsName() == OS.WINDOWS;
	}

	public static boolean isUnderMaxOSX() {
		return EnvironmentUtils.getOsName() == OS.MAC;
	}

	/**
	 * 
	 * @param location
	 *            path in the registry
	 * @param key
	 *            registry key
	 * @return registry value or null if not found
	 */
	public static final String readRegistry(String location, String key) {
		try {
			// Run reg query, then read output with StreamReader (internal
			// class)
			Process process = Runtime.getRuntime().exec(
					"reg query " + '"' + location + "\" /v " + key);

			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String output = reader.getResult();

			// Output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("\t")) {
				return null;
			}

			// Parse out the value
			String[] parsed = output.split("\t");
			return parsed[parsed.length - 1];
		} catch (Exception e) {
			return null;
		}

	}

	public static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw = new StringWriter();;

		public StreamReader(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
			}
		}

		public String getResult() {
			return sw.toString();
		}
	}

	public static String getZendServerInstallLocation() {
		return readRegistry("HKLM\\Software\\Zend Technologies\\ZendServer\\",
				"InstallLocation");

	}

}
