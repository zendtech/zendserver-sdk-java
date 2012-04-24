/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import org.eclipse.core.resources.IFile;

/**
 * Represents event details provided by Zend Server. It provides interface to
 * get project resource which is related to particular event and event type.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface IEventDetails {

	/**
	 * @return project name
	 */
	String getProjectName();

	/**
	 * @return full path of source file on a server
	 */
	String getSourceFile();

	/**
	 * @return line number
	 */
	long getLine();

	/**
	 * Creates project relative path based on source file path and project name.
	 * 
	 * @return project relative path to the source file
	 */
	String getProjectRelativePath();

	/**
	 * Returns resource for a source file for particular project in the
	 * workspace.
	 * 
	 * @return workspace resource
	 */
	IFile getResource();

	/**
	 * @return event's type
	 * @see EventType
	 */
	EventType getType();

	/**
	 * Checks source availability.
	 * 
	 * @return <code>true</code> if source is available; otherwise return
	 *         <code>false</code>
	 */
	boolean isAvailable();

}