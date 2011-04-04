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
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.values.ErrorCode;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestClusterServices extends TestCommonServices {

	@Test
	public void testClusterGetServerStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterGetServerStatus(), "clusterGetServerStatus",
				ResponseCode.OK);
		ServersList clusterServerStatus = Configuration.getClient()
				.clusterGetServerStatus();
		DataUtils.checkValidClusterServerStatus(clusterServerStatus);
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterGetServerStaus405() throws WebApiException,
			MalformedURLException {
		initErrorMock(handler.clusterGetServerStatus(),
				"clusterGetServerStatus", ErrorCode.notImplementedByEdition);
		Configuration.getClient().clusterGetServerStatus();
	}

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

}
