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
import org.zend.webapi.internal.core.connection.request.ClusterAddServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterDisableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterEnableServerRequest;
import org.zend.webapi.internal.core.connection.request.ClusterRemoveServerRequest;
import org.zend.webapi.test.Configuration;

public class TestClusterRequestParams extends TestCommonRequestParams {

	@Test
	public void testClusterAddServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterAddServer(), "clusterAddServer",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.CLUSTER_ADD_SERVER, new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ClusterAddServerRequest r = (ClusterAddServerRequest) request;
						r.setDoStart(true);
						r.setGuiPassword("passwd");
						r.setPropagateSettings(false);
						r.setServerName("zend1");
						r.setServerUrl("https://www-02.local:10082/ZendServer");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals(
				"doRestart=TRUE&guiPassword=passwd&propagateSettings=FALSE"
						+ "&serverName=zend1&serverUrl=https://www-02.local:10082/ZendServer",
				request.getParametersAsString());
	}

	@Test
	public void testClusterRemoveServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterRemoveServer(), "clusterRemoveServer",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.CLUSTER_REMOVE_SERVER,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ClusterRemoveServerRequest r = (ClusterRemoveServerRequest) request;
						r.setServerId("1");
						r.setForce(true);
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals("serverId=1&force=TRUE",
				request.getParametersAsString());
	}

	@Test
	public void testClusterDisableServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterDisableServer(), "clusterDisableServer",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.CLUSTER_DISABLE_SERVER,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ClusterDisableServerRequest r = (ClusterDisableServerRequest) request;
						r.setServerId("1");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals("serverId=1", request.getParametersAsString());
	}

	@Test
	public void testClusterEnableServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterEnableServer(), "clusterEnableServer",
				ResponseCode.OK);
		IResponse response = Configuration.getClient().handle(
				WebApiMethodType.CLUSTER_ENABLE_SERVER,
				new IRequestInitializer() {

					public void init(IRequest request) throws WebApiException {
						ClusterEnableServerRequest r = (ClusterEnableServerRequest) request;
						r.setServerId("1");
					}
				});

		final AbstractRequest request = (AbstractRequest) response.getRequest();
		Assert.assertEquals("serverId=1", request.getParametersAsString());
	}

}
