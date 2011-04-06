/*******************************************************************************
 * Copyright (c) Feb 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.zend.webapi.core.configuration.ClientConfiguration;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.ServerConfig;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.dispatch.IServiceDispatcher;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.request.IRequestInitializer;
import org.zend.webapi.core.connection.request.RequestFactory;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.service.WebApiMethodType;
import org.zend.webapi.internal.core.connection.ServiceDispatcher;
import org.zend.webapi.internal.core.connection.request.ClusterAddServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterDisableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterEnableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterGetServerStatusRequest;
import org.zend.webapi.internal.core.connection.request.ClusterRemoveServerRequest;
import org.zend.webapi.internal.core.connection.request.ConfigurationImportRequest;
import org.zend.webapi.internal.core.connection.request.RestartPhpRequest;

/**
 * Client for accessing Zend Server Web API. All service calls made using this
 * client are blocking, and will not return until the service call completes.
 * 
 * Zend Server Web API simple web service interface allows you to obtain and
 * configure your Zend Server and Zend Server cluster manager. It provides you
 * with complete control of your PHP Web applications servers and lets you run
 * on Zend's proven computing environment.
 * 
 * Zend Server Web API is intended to allow automation of the management and
 * deployment of Zend Server and Zend Server Cluster Manager, and allow
 * integration with other Zend or 3rd party software. Call a specific service
 * method
 * 
 * @author Roy, 2011
 * 
 */
public class WebApiClient {

	private static final WebApiVersion DEFAULT_VERSION = WebApiVersion.V1;

	/**
	 * credentials of this client
	 */
	private final WebApiCredentials credentials;

	/**
	 * Client configuration of this instance
	 */
	private final ClientConfiguration clientConfiguration;

	/**
	 * Constructs a new client to invoke service methods on Zend Server API
	 * using the specified account credentials and configurations.
	 * 
	 * @param credentials
	 * @param userAgent
	 */
	public WebApiClient(WebApiCredentials credentials,
			ClientConfiguration clientConfiguration) {
		this.credentials = credentials;
		this.clientConfiguration = clientConfiguration;
	}

	/**
	 * Constructs a new client to invoke service methods on Zend Server API
	 * using the the specified host and credentials.
	 * 
	 * @param credentials
	 * @param userAgent
	 * @throws MalformedURLException
	 */
	public WebApiClient(WebApiCredentials credentials, String host)
			throws MalformedURLException {
		this(credentials, new ClientConfiguration(new URL(host)));
	}

	/**
	 * Get information about the system, including Zend Server edition and
	 * version, PHP version, licensing information etc. In general this method
	 * should be available and produce similar output on all Zend Server
	 * systems, and be as future compatible as possible
	 * 
	 * @see WebApiMethodType#GET_SYSTEM_INFO
	 * @return
	 */
	public SystemInfo getSystemInfo() throws WebApiException {
		final IResponse handle = this.handle(WebApiMethodType.GET_SYSTEM_INFO,
				null);
		return (SystemInfo) handle.getData();
	}

	/**
	 * Get the list of servers in the cluster and the status of each one. On a
	 * ZSCM with no valid license, this operation will fail. Note that this
	 * operation will cause Zend Server Cluster Manager to check the status of
	 * servers and return fresh, non-cached information. This is different from
	 * the Servers List tab in the GUI, which may present cached information.
	 * Users interested in reducing load by caching this information should do
	 * in their own code.
	 * 
	 * @see WebApiMethodType#CLUSTER_GET_SERVER_STATUS
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList clusterGetServerStatus(final String... servers)
			throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CLUSTER_GET_SERVER_STATUS,
				servers.length == 0 ? null : new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						((ClusterGetServerStatusRequest) request)
								.setServers(servers);
					}
				});
		return (ServersList) handle.getData();
	}

	/**
	 * Add a new server to the cluster. On a ZSCM with no valid license, this
	 * operation will fail.
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterAddServer(final String serverName,
			final String serverUrl, final String guiPassword,
			final Boolean propagateSettings, final Boolean doRestart)
			throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CLUSTER_ADD_SERVER, new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						final ClusterAddServerRequest r = ((ClusterAddServerRequest) request)
								.setServerName(serverName)
								.setServerUrl(serverUrl)
								.setGuiPassword(guiPassword);
						if (propagateSettings != null)
							r.setPropagateSettings(propagateSettings);
						if (doRestart != null)
							r.setDoStart(doRestart);
					}
				});
		return (ServerInfo) handle.getData();
	}

	/**
	 * Add a new server to the cluster. On a ZSCM with no valid license, this
	 * operation will fail.
	 * 
	 * doRestart parameter value is not specified (for more details
	 * {@link ClusterAddServerRequest}
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterAddServer(final String serverName,
			final String serverUrl, final String guiPassword,
			final Boolean propagateSettings) throws WebApiException {
		return clusterAddServer(serverName, serverUrl, guiPassword,
				propagateSettings, null);
	}

	/**
	 * Add a new server to the cluster. On a ZSCM with no valid license, this
	 * operation will fail.
	 * 
	 * doRestart and propagateSettings parameter values are not specified (for
	 * more details {@link ClusterAddServerRequest}
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterAddServer(final String serverName,
			final String serverUrl, final String guiPassword)
			throws WebApiException {
		return clusterAddServer(serverName, serverUrl, guiPassword, null, null);
	}

	/**
	 * Remove a server from the cluster. The removal process may be asynchronous
	 * if Session Clustering is used – if this is the case, the initial
	 * operation will return an HTTP 202 response. As long as the server is not
	 * fully removed, further calls to remove the same server should be
	 * idempotent. On a ZSCM with no valid license, this operation will fail.
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterRemoveServer(final String serverId,
			final Boolean force) throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CLUSTER_REMOVE_SERVER,
				new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						final ClusterRemoveServerRequest r = ((ClusterRemoveServerRequest) request)
								.setServerId(serverId);
						if (force != null)
							r.setForce(force);
					}
				});
		return (ServerInfo) handle.getData();
	}

	/**
	 * Remove a server from the cluster. The removal process may be asynchronous
	 * if Session Clustering is used – if this is the case, the initial
	 * operation will return an HTTP 202 response. As long as the server is not
	 * fully removed, further calls to remove the same server should be
	 * idempotent. On a ZSCM with no valid license, this operation will fail.
	 * 
	 * propagateSettings parameter value is not specified. (for more details
	 * {@link ClusterRemoveServerRequest}
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterRemoveServer(final String serverId)
			throws WebApiException {
		return clusterRemoveServer(serverId, null);
	}

	/**
	 * Remove a server from the cluster. The removal process may be asynchronous
	 * if Session Clustering is used – if this is the case, the initial
	 * operation will return an HTTP 202 response. As long as the server is not
	 * fully removed, further calls to remove the same server should be
	 * idempotent. On a ZSCM with no valid license, this operation will fail.
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterDisableServer(final String serverId)
			throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CLUSTER_DISABLE_SERVER,
				new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						((ClusterDisableServerRequest) request)
								.setServerId(serverId);
					}
				});
		return (ServerInfo) handle.getData();
	}

	/**
	 * Re-enable a cluster member. This process may be asynchronous if Session
	 * Clustering is used – if this is the case, the initial operation will
	 * return an HTTP 202 response. This action is idempotent. Running it on an
	 * enabled server will result in a “200 OK” response with no
	 * consequences. On a ZSCM with no valid license, this operation will fail.
	 * 
	 * @return server info
	 * @throws WebApiException
	 */
	public ServerInfo clusterEnableServer(final String serverId)
			throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CLUSTER_ENABLE_SERVER,
				new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						((ClusterEnableServerRequest) request)
								.setServerId(serverId);
					}
				});
		return (ServerInfo) handle.getData();
	}

	/**
	 * Restart PHP on all servers or on specified servers in the cluster. A 202
	 * response in this case does not always indicate a successful restart of
	 * all servers, and the user is advised to check the server(s) status again
	 * after a few seconds using the clusterGetServerStatus command.
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList restartPhp(final Boolean parallelRestart,
			final String... servers) throws WebApiException {
		final IResponse handle = this.handle(WebApiMethodType.RESTART_PHP,
				servers == null ? null : new IRequestInitializer() {
					public void init(IRequest request) throws WebApiException {
						((RestartPhpRequest) request).setServers(servers)
								.setParallelRestart(parallelRestart);
					}
				});
		return (ServersList) handle.getData();
	}

	/**
	 * Restart PHP on all servers or on specified servers in the cluster. A 202
	 * response in this case does not always indicate a successful restart of
	 * all servers, and the user is advised to check the server(s) status again
	 * after a few seconds using the clusterGetServerStatus command.
	 * 
	 * parallelRestart parameter calue is not specified. for more detailed see
	 * {@link RestartPhpRequest}
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList restartPhp(final String... servers)
			throws WebApiException {
		return restartPhp(false, servers);
	}

	/**
	 * Restart PHP on all servers or on specified servers in the cluster. A 202
	 * response in this case does not always indicate a successful restart of
	 * all servers, and the user is advised to check the server(s) status again
	 * after a few seconds using the clusterGetServerStatus command.
	 * 
	 * parallelRestart and servers parameter values are not specified. for more
	 * detailed see {@link RestartPhpRequest}
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList restartPhp() throws WebApiException {
		return restartPhp((Boolean) null, (String[]) null);
	}

	/**
	 * export the current server / cluster configuration into a file.
	 * 
	 * @return server config
	 * @throws WebApiException
	 */
	public ServerConfig configuratioExport() throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CONFIGURATION_EXPORT, null);
		return (ServerConfig) handle.getData();
	}

	/**
	 * Import a saved configuration snapshot into the server
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList configuratioImport(final File configFile,
			final Boolean ignoreSystemMismatch) throws WebApiException {
		final IResponse handle = this.handle(
				WebApiMethodType.CONFIGURATION_IMPORT,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						((ConfigurationImportRequest) request)
								.setFile(configFile);

						if (ignoreSystemMismatch != null)
							((ConfigurationImportRequest) request)
									.setIgnoreSystemMismatch(ignoreSystemMismatch);
					}
				});
		return (ServersList) handle.getData();
	}

	/**
	 * Import a saved configuration snapshot into the server
	 * 
	 * ignoreSystemMismatch parameter value is not specified. see
	 * {@link ConfigurationImportRequest} for more details.
	 * 
	 * @return servers list
	 * @throws WebApiException
	 */
	public ServersList configuratioImport(final File configFile)
			throws WebApiException {
		return configuratioImport(configFile, null);
	}

	/**
	 * Zend Server Web API is intended to allow automation of the management and
	 * deployment of Zend Server and Zend Server Cluster Manager, and allow
	 * integration with other Zend or 3rd party software. Call a specific
	 * service method
	 * 
	 * @param methodType
	 *            the method to be called
	 * @param initializer
	 *            initializer of this request
	 * @return the response object
	 * @throws WebApiException
	 */
	public IResponse handle(WebApiMethodType methodType,
			IRequestInitializer initializer) throws WebApiException {

		// create request
		IRequest request = RequestFactory.createRequest(methodType,
				DEFAULT_VERSION, new Date(), this.credentials.getKeyName(),
				this.clientConfiguration.getUserAgent(),
				this.clientConfiguration.getHost().toString(),
				this.credentials.getSecretKey());

		if (initializer != null) {
			initializer.init(request);
		}

		// apply request
		IServiceDispatcher dispatcher = new ServiceDispatcher();
		IResponse response = dispatcher.dispatch(request);

		// return response data to caller
		return response;
	}

}
