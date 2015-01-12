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

		SERVER_CONFIG,

		DEPLOYED_VERSION,

		DEPLOYED_VERSIONS_LIST,

		APPLICATION_INFO,

		APPLICATIONS_LIST,

		APPLICATION_SERVER,

		APPLICATION_SERVERS_LIST,

		REQUEST_SUMMARY,

		ISSUE,

		ISSUE_DETAILS,

		ROUTE_DETAIL,

		ROUTE_DETAILS,

		EVENTS_GROUP,

		EVENTS_GROUPS,

		EVENTS_GROUP_DETAILS,

		EVENT,

		EVENTS,

		BACKTRACE,

		PARAMETER,

		PARAMETER_LIST,

		SUPER_GLOBALS,

		STEP,

		CODE_TRACING_STATUS,

		CODE_TRACE,

		CODE_TRACING_LIST,

		CODE_TRACE_FILE,

		ISSUE_LIST,

		ISSUE_FILE,

		DEBUG_REQUEST,

		PROFILE_REQUEST,

		GENERAL_DETAILS,

		DEBUG_MODE,

		LIBRARY_SERVER,

		LIBRARY_SERVERS,

		LIBRARY_VERSION,

		LIBRARY_VERSIONS,

		LIBRARY_INFO,

		LIBRARY_LIST,

		LIBRARY_FILE,

		APIKEY,

		BOOTSTRAP,

		VHOSTS_LIST,
		
		VHOST_INFO

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
