/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.logger;

/**
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface ILogger {

	/**
	 * Logs debug message.
	 * 
	 * @param message
	 */
	public void debug(Object message);

	/**
	 * Logs information message.
	 * 
	 * @param message
	 */
	public void info(Object message);

	/**
	 * Logs warning message.
	 * 
	 * @param message
	 */
	public void warning(Object message);

	/**
	 * Logs error message.
	 * 
	 * @param message
	 */
	public void error(Object message);

	/**
	 * Returns logger instance for specified name. CreatorName is the name of a
	 * class which wants to use logging mechanism. The usage of this value
	 * depends on the logger implementation. If logger does not required it, it
	 * will be ignored.
	 * @param verbose 
	 * 
	 * @param name
	 * @return
	 */
	public ILogger getLogger(String creatorName, boolean verbose);

}
