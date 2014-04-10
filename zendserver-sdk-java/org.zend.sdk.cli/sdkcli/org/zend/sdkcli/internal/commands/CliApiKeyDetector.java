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

	private String username;
	private String password;

	public CliApiKeyDetector(String username, String password) {
		super("http://localhost:10081/ZendServer");
		this.username = username;
		this.password = password;
	}

	public String[] getServerCredentials(final String message, String serverName) {
		if (username == null || password == null) {
			if (serverName != null) {
				System.console().printf(
						"Zend Server Credentials for " + serverName + ":\n");
			} else {
				System.console().printf("Zend Server Credentials:\n");
			}
		}
		if (username == null) {
			username = String.valueOf(System.console().readLine("Username: "));
		}
		if (password == null) {
			password = String.valueOf(System.console().readPassword(
					"Password: "));
		}
		if (username == null || username.trim().isEmpty() || password == null
				|| password.trim().isEmpty()) {
			return null;
		}
		return new String[] { username, password };
	}

}
