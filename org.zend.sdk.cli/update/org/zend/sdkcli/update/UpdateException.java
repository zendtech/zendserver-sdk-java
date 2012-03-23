/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update;

/**
 * 
 * Represents general exception which may be thrown duirng update process.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateException extends Exception {

	private static final long serialVersionUID = 8208759591470984865L;

	public UpdateException(Exception e) {
		super(e);
	}

	public UpdateException(String message) {
		super(message);
	}

	public UpdateException(String message, Exception e) {
		super(message, e);
	}

}
