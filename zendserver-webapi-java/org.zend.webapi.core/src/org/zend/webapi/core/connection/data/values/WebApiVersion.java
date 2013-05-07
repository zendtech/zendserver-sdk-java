/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Represents version in the system
 * 
 */
public enum WebApiVersion {

	V1("1.0"),

	V1_1("1.1"),

	V1_2("1.2"),
	
	V1_3("1.3"),
	
	V1_4("1.4"),
	
	V1_5("1.5"),

	UNKNOWN("UNKNOWN");

	private final String versionName;
	private static final String prefix = "application/vnd.zend.serverapi+xml;version=";

	private WebApiVersion(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getFullName() {
		return prefix + getVersionName();
	}

	public static WebApiVersion byFullName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		WebApiVersion[] values = values();
		for (WebApiVersion webApiVersion : values) {
			if (name.equals(webApiVersion.getFullName())) {
				return webApiVersion;
			}
		}
		return UNKNOWN;
	}
}
