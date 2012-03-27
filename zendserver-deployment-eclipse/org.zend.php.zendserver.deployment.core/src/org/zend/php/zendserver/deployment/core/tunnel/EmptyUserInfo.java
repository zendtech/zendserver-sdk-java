/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import com.jcraft.jsch.UserInfo;

public class EmptyUserInfo implements UserInfo {

	public String getPassphrase() {
		return null;
	}

	public String getPassword() {
		return null;
	}

	public boolean promptPassword(String message) {
		return false;
	}

	public boolean promptPassphrase(String message) {
		return false;
	}

	public boolean promptYesNo(String message) {
		return true;
	}

	public void showMessage(String message) {
	}

}