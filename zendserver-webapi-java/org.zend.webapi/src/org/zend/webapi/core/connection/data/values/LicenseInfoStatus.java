/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

public enum LicenseInfoStatus {
	
	NOT_REQUIRED("notRequired", "this edition does not require this license type"),

	OK("OK", "licensed and working"),

	INVALID ("invalid", "license is invalid"),

	EXPIRED ("expired", "license has expired"),

	SERVER_LIMIT_EXCEEDED ("serverLimitExceeded", "ZSCM server limit exceeded"), 
	
	UNKNOWN ("Unknown", "Unknown");

	private final String description;
	private final String name;

	private LicenseInfoStatus(String name, String description) {
		this.description = description;
		this.name = name;
	}

	public static LicenseInfoStatus byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}

		LicenseInfoStatus[] values = values();
		for (int i = 0; i < values.length; i++) {
			LicenseInfoStatus status = values[i];
			if (name.equals(status.name)) {
				return status;
			}
		}
		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}