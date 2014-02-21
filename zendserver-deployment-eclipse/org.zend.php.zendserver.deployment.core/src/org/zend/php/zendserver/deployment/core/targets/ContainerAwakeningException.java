/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

public class ContainerAwakeningException extends WebApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -379093380135651630L;

	public String getMessage() {
		return Messages.ContainerWakeUpException_Message;
	}

	public ResponseCode getResponseCode() {
		return ResponseCode.UNKNOWN;
	}

}
