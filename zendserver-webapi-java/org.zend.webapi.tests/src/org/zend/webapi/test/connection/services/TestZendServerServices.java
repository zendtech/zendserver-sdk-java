package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestZendServerServices extends TestCommonServices {

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterAddServer() throws WebApiException,
			MalformedURLException {
		initErrorMock(handler.clusterAddServer(), "clusterAddServer",
				ResponseCode.NOT_IMPLEMENTED_BY_EDITION);
		String serverName = "newServer";
		String serverUrl = "https://www-05.local:10082/ZendServer";
		String guiPassword = "passwd";
		Configuration.getClient().clusterAddServer(serverName, serverUrl,
				guiPassword);
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterRemoveServer() throws WebApiException,
			MalformedURLException {
		initErrorMock(handler.clusterRemoveServer(), "clusterRemoveServer",
				ResponseCode.NOT_IMPLEMENTED_BY_EDITION);
		ServerInfo serverInfo = Configuration.getClient().clusterRemoveServer(
				"zend1");
		DataUtils.checkValidServerInfo(serverInfo);
		Assert.assertEquals(serverInfo.getStatus(), ServerStatus.SHUTTING_DOWN);
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterDisableServer() throws WebApiException,
			MalformedURLException {
		initErrorMock(handler.clusterDisableServer(), "clusterDisableServer",
				ResponseCode.NOT_IMPLEMENTED_BY_EDITION);
		ServerInfo serverInfo = Configuration.getClient().clusterDisableServer(
				"zend1");
		DataUtils.checkValidServerInfo(serverInfo);
		Assert.assertEquals(serverInfo.getStatus(), ServerStatus.DISABLED);
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterEnableServer() throws WebApiException,
			MalformedURLException {
		initErrorMock(handler.clusterEnableServer(), "clusterEnableServer",
				ResponseCode.NOT_IMPLEMENTED_BY_EDITION);
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
