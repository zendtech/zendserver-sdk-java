/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

public enum ServerStatus {

	OK("OK"),

	SHUTTING_DOWN("shuttingDown"),

	STARTING_UP("startingUp"),

	PENDING_RESTART("pendingRestart"),

	RESTARTING("restarting"),

	MISCONFIGURED("misconfigured"),

	EXTENSION_MISMATCH("extensionMismatch"),

	DAEMON_MISMATCH("daemonMismatch"),

	APPLICATION_MISMATCH("applicationMismatch"),

	NOT_RESPONDING("notResponding"),

	DISABLED("disabled"),

	REMOVED("removed"),

	UNKNOWN("unknown");

	private final String name;

	private ServerStatus(String name) {
		this.name = name;
	}

	public static ServerStatus byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}

		ServerStatus[] values = values();
		for (ServerStatus serverStatus : values) {
			if (name.equals(serverStatus.getName())) {
				return serverStatus;
			}
		}

		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

}