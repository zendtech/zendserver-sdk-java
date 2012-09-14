/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Zend Server version.
 * 
 * @author Wojciech Galanciak, 2012
 *
 */
public enum ZendServerVersion {

	v5_0_0("5.0.0"),

	v5_5_0("5.5.0"),

	v5_6_0("5.6.0"),
	
	v6_0_0("6.0.0"),

	UNKNOWN("Unknown");

	private final String name;

	private ZendServerVersion(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static ZendServerVersion byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}

		ZendServerVersion[] values = values();
		for (int i = 0; i < values.length; i++) {
			ZendServerVersion version = values[i];
			if (name.equals(version.name)) {
				return version;
			}
		}
		return UNKNOWN;
	}

}