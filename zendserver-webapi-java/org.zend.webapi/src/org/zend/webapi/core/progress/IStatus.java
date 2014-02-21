/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.webapi.core.progress;

/**
 * Represents status of a progress in a particular process.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IStatus {

	/**
	 * @return status code of a process
	 */
	StatusCode getCode();

	/**
	 * @return title of a status; can be <code>null</code>
	 */
	String getTitle();

	/**
	 * @return message related to a status; can be <code>null</code>
	 */
	String getMessage();

	/**
	 * @return total work to be done
	 */
	int getTotalWork();

	/**
	 * @return {@link Throwable} instance if status is related to exceptional
	 *         situation; can be null
	 */
	Throwable getThrowable();

}
