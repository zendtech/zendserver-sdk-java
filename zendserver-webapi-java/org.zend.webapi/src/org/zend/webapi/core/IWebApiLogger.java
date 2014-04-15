/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
public interface IWebApiLogger {

	/**
	 * Log error message.
	 * 
	 * @param message
	 */
	void logError(String message);

	/**
	 * Log an exception.
	 * 
	 * @param e
	 */
	void logError(Throwable e);

	/**
	 * Log exception with a message.
	 * 
	 * @param message
	 * @param e
	 */
	void logError(String message, Throwable e);

	/**
	 * Log a warning.
	 * 
	 * @param message
	 */
	void logWarning(String message);

	/**
	 * Log an information.
	 * 
	 * @param message
	 */
	void logInfo(String message);

}
