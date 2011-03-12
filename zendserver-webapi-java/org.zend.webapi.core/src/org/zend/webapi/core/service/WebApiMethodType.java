/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.service;

import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.internal.core.connection.request.ClusterAddServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterDisableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterEnableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterGetServerStatusRequest;
import org.zend.webapi.internal.core.connection.request.ClusterRemoveServerRequest;
import org.zend.webapi.internal.core.connection.request.ConfigurationExportRequest;
import org.zend.webapi.internal.core.connection.request.ConfigurationImportRequest;
import org.zend.webapi.internal.core.connection.request.GetSystemInfoRequest;
import org.zend.webapi.internal.core.connection.request.RestartPhpRequest;

/**
 * The Zend Server Web API is intended to allow automation of the management and
 * deployment of Zend Server and Zend Server Cluster Manager, and allow
 * integration with other Zend or 3rd party software. <br>
 * 
 * Each registered service method is represented by its name and request and
 * response properties
 * 
 * @author Roy, 2011
 * @see IRequest
 * @see IResponseData
 * 
 */
public enum WebApiMethodType {

	/**
	 * @see GetSystemInfoRequest
	 */
	GET_SYSTEM_INFO("getSystemInfo", GetSystemInfoRequest.class),

	/**
	 * @see ClusterGetServerStatusRequest
	 */
	CLUSTER_GET_SERVER_STATUS("clusterGetServerStatus",
			ClusterGetServerStatusRequest.class),

	/**
	 * @see ClusterAddServerRequest
	 */
	CLUSTER_ADD_SERVER("clusterAddServer", ClusterAddServerRequest.class),

	/**
	 * @see ClusterRemoveServerRequest
	 */
	CLUSTER_REMOVE_SERVER("clusterRemoveServer",
			ClusterRemoveServerRequest.class),

	/**
	 * @see ClusterDisableServerRequest
	 */
	CLUSTER_DISABLE_SERVER("clusterDisableServer",
			ClusterDisableServerRequest.class),

	/**
	 * @see ClusterEnableServerRequest
	 */
	CLUSTER_ENABLE_SERVER("clusterEnableServer",
			ClusterEnableServerRequest.class),

	/**
	 * @see RestartPhpRequest
	 */
	RESTART_PHP("restartPhp", RestartPhpRequest.class),

	/**
	 * @see ConfigurationExportRequest
	 */
	CONFIGURATION_EXPORT("configurationExport",
			ConfigurationExportRequest.class),

	/**
	 * @see ConfigurationImportRequest
	 */
	CONFIGURATION_IMPORT("configurationImport",
			ConfigurationImportRequest.class);

	/**
	 * Name of the service
	 */
	private final String name;

	/**
	 * Request class
	 */
	private final Class<? extends IRequest> requestClass;

	private WebApiMethodType(String name, Class<? extends IRequest> requestClass) {
		this.name = name;
		this.requestClass = requestClass;
	}

	/**
	 * @return name of the method
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return request bound to this method
	 */
	public Class<? extends IRequest> getRequestClass() {
		return requestClass;
	}

}
