/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

public interface IResponseData {

	public enum ResponseType {

		LICENSE_INFO,

		MESSAGE_LIST,

		SERVER_INFO,

		SERVERS_LIST,

		SYSTEM_INFO,
		
		SERVER_CONFIG
	}

	/**
	 * Type of the data
	 * 
	 * @return
	 */
	public abstract ResponseType getType();

	/**
	 * Visitor for the data
	 * 
	 * @param visitor
	 * @return
	 */
	public abstract boolean accept(IResponseDataVisitor visitor);
}
