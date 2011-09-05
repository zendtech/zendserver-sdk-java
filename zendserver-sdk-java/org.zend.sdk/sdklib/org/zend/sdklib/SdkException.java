/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib;

public class SdkException extends Exception {

	private static final long serialVersionUID = 3663533266095285157L;

	public SdkException(Exception e) {
		super(e);
	}

	public SdkException(String message) {
		super(message);
	}
	
	public SdkException(String message, Exception e) {
		super(message, e);
	}
	
}
