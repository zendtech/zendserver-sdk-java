/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdklib.internal.target.ApiKeyDetector;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class CliApiKeyDetector extends ApiKeyDetector {

	public CliApiKeyDetector(String username, String password) {
		super(username, password, "http://localhost:10081/ZendServer");
	}

	public void getServerCredentials(final String message, String serverName) {
		if (getUsername() == null || getPassword() == null) {
			if (serverName != null) {
				System.console().printf(
						"Zend Server Credentials for " + serverName + ":\n");
			} else {
				System.console().printf("Zend Server Credentials:\n");
			}
		}
		if (getUsername() == null) {
			setUsername(String.valueOf(System.console().readLine("Username: ")));
		}
		if (getPassword() == null) {
			setPassword(String.valueOf(System.console().readPassword(
					"Password: ")));
		}
	}

}
