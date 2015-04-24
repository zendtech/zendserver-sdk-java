/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.logger;

/**
 * General class which manages logging system.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Log {

	private ILogger logger;

	private static Log log;

	private Log() {
	}

	/**
	 * @return instance of the logger
	 */
	public static Log getInstance() {
		if (log == null) {
			log = new Log();
		}
		return log;
	}

	/**
	 * Registers logger which will be used for logging.
	 * 
	 * @param logger
	 */
	public void registerLogger(ILogger logger) {
		this.logger = logger;
	}

	/**
	 * @param creatorName
	 *            - name of a class which wants to use logging mechanism. The
	 *            usage of this value depends on the logger implementation.
	 * @return logger
	 */
	public ILogger getLogger(String creatorName) {
		return getLogger(creatorName, false);
	}
	
	/**
	 * @param creatorName
	 * @param verbose
	 * @return
	 */
	public ILogger getLogger(String creatorName, boolean verbose) {
		if (logger == null) {
			throw new IllegalStateException(
					"Logger has not been registered yet, register one by"
					+ " \"Log.getInstance().registerLogger(new CliLogger());\"");
		}
		return logger.getLogger(creatorName, verbose);
	}

}
