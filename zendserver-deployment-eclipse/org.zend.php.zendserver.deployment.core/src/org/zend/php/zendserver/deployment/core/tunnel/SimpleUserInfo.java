/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import com.jcraft.jsch.UserInfo;

/**
 * Simple implementation of {@link UserInfo} interface. It provides user
 * password.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class SimpleUserInfo implements UserInfo {

	private String password;

	public SimpleUserInfo() {
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public boolean promptPassword(String message) {
		return false;
	}

	@Override
	public boolean promptPassphrase(String message) {
		return false;
	}

	@Override
	public boolean promptYesNo(String message) {
		return true;
	}

	@Override
	public void showMessage(String message) {
	}

}