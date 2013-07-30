/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.request.IRequest;
import org.zend.webapi.core.connection.request.IRequestInitializer;
import org.zend.webapi.core.connection.response.IResponse;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.core.service.WebApiMethodType;
import org.zend.webapi.internal.core.connection.request.AbstractRequest;
import org.zend.webapi.internal.core.connection.request.ApplicationGetStatusRequest;
import org.zend.webapi.internal.core.connection.request.ClusterGetServerStatusRequest;
import org.zend.webapi.internal.core.connection.request.RestartPhpRequest;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;

public class TestCommonRequestParams extends AbstractTestServer {

	@Test
	public void testClusterGetServerStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterGetServerStatus(), "clusterGetServerStatus",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.CLUSTER_GET_SERVER_STATUS,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ClusterGetServerStatusRequest r = (ClusterGetServerStatusRequest) request;
						r.setServers("my-server", "your-server");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals(
				"servers%5B0%5D=my-server&servers%5B1%5D=your-server",
				request.getParametersAsString());
	}

	@Test
	public void testRestartPhpParams() throws WebApiException,
			MalformedURLException {
		initMock(handler.restartPhp(), "restartPhp", ResponseCode.ACCEPTED);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.RESTART_PHP, new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						RestartPhpRequest r = (RestartPhpRequest) request;
						r.setParallelRestart(true);
						r.setServers("my-server");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals("parallelRestart=TRUE&servers%5B0%5D=my-server",
				request.getParametersAsString());
	}

	@Test
	public void testApplicationGetStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.APPLICATION_GET_STATUS,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ApplicationGetStatusRequest r = (ApplicationGetStatusRequest) request;
						r.setApplications("test1", "test2");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals(
				"applications%5B0%5D=test1&applications%5B1%5D=test2",
				request.getParametersAsString());
	}

}
