/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;


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

	public static boolean isUACEnabled() {
		String keyPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System\\EnableLUA";
		String[] keyPathArray = keyPath.split("\\\\");
		
		RegistryValue value;
		try {
			RegistryKey key = Registry.HKEY_LOCAL_MACHINE;
			for (int i = 0; i < keyPathArray.length - 1; i++) {
				key = key.openSubKey(keyPathArray[i]);
			}
			value = key.getValue(keyPathArray[keyPathArray.length - 1]);
		} catch (NoSuchValueException e) {
			return false;
		} catch (RegistryException e) {
			return false;
		}
		byte[] data = value.getByteData();
		if (data == null) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			if (data[i] > 0) {
				return true;
			}
		}
		
		return false;
	}
}
