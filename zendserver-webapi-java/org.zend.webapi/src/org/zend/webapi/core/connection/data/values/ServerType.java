/*******************************************************************************
 * Copyright (c) Aug 17, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Server type.
 * 
 * @author Wojciech Galanciak, 2012
 *
 */
public enum ServerType {

	ZEND_SERVER("ZendServer"),

	ZEND_SERVER_MANAGER("ZendServerManager");
	
	private final String serverType;
	
	private ServerType(String versionName) {
		this.serverType = versionName;
	}

	public String getName() {
		return serverType;
	}

	public static ServerType byServerName(String type) {
		if (type == null) {
			return null;
		}
		ServerType[] values = values();
		for (ServerType serverType : values) {
			if (type.equals(serverType.getName())) {
				return serverType;
			}
		}
		return null;
	}
	
}
