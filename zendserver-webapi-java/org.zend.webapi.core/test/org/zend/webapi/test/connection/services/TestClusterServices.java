/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestClusterServices extends TestCommonServices {

	@Test
	public void testClusterAddServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterAddServer(), "clusterAddServer",
				ResponseCode.OK);
		String serverName = "newServer";
		String serverUrl = "https://www-05.local:10082/ZendServer";
		String guiPassword = "passwd";
		ServerInfo serverInfo = Configuration.getClient().clusterAddServer(
				serverName, serverUrl, guiPassword);
		DataUtils.checkValidServerInfo(serverInfo);
	}

	@Test
	public void testClusterAddServerPropagate() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterAddServer(), "clusterAddServer",
				ResponseCode.OK);
		String serverName = "newServer";
		String serverUrl = "https://www-05.local:10082/ZendServer";
		String guiPassword = "passwd";
		ServerInfo serverInfo = Configuration.getClient().clusterAddServer(
				serverName, serverUrl, guiPassword, true);
		DataUtils.checkValidServerInfo(serverInfo);
	}

	@Test
	public void testClusterAddServerPropagateAndRestart()
			throws WebApiException, MalformedURLException {
		initMock(handler.clusterAddServer(), "clusterAddServer",
				ResponseCode.OK);
		String serverName = "newServer";
		String serverUrl = "https://www-05.local:10082/ZendServer";
		String guiPassword = "passwd";
		ServerInfo serverInfo = Configuration.getClient().clusterAddServer(
				serverName, serverUrl, guiPassword, true, true);
		DataUtils.checkValidServerInfo(serverInfo);
	}

	@Test
	public void testClusterRemoveServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterRemoveServer(), "clusterRemoveServer",
				ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient().clusterRemoveServer(
				"zend1");
		DataUtils.checkValidServerInfo(serverInfo);
		Assert.assertEquals(serverInfo.getStatus(), ServerStatus.SHUTTING_DOWN);
	}

	@Test
	public void testClusterRemoveServerForce() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterRemoveServer(), "clusterRemoveServer",
				ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient().clusterRemoveServer(
				"zend1", true);
		DataUtils.checkValidServerInfo(serverInfo);
		Assert.assertEquals(serverInfo.getStatus(), ServerStatus.SHUTTING_DOWN);
	}

	@Test
	public void testClusterDisableServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterDisableServer(), "clusterDisableServer",
				ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient().clusterDisableServer(
				"zend1");
		DataUtils.checkValidServerInfo(serverInfo);
		Assert.assertEquals(serverInfo.getStatus(), ServerStatus.DISABLED);
	}

	@Test
	public void testClusterEnableServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterEnableServer(), "clusterEnableServer",
				ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient().clusterEnableServer(
				"zend1");
		DataUtils.checkValidServerInfo(serverInfo);
		ServerStatus status = serverInfo.getStatus();
		boolean isCorrect = status != ServerStatus.SHUTTING_DOWN
				&& status != ServerStatus.RESTARTING
				&& status != ServerStatus.DISABLED
				&& status != ServerStatus.REMOVED
				&& status != ServerStatus.UNKNOWN;
		Assert.assertTrue(isCorrect);
	}

	@Test
	public void testClusterReconfigureServer() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterReconfigureServer(),
				"clusterReconfigureServer", ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient()
				.clusterReconfigureServer("zend1");
		DataUtils.checkValidServerInfo(serverInfo);
	}

	@Test
	public void testClusterReconfigureServerDoRestart() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterReconfigureServer(),
				"clusterReconfigureServer", ResponseCode.OK);
		ServerInfo serverInfo = Configuration.getClient()
				.clusterReconfigureServer("zend1", true);
		DataUtils.checkValidServerInfo(serverInfo);
	}

}
