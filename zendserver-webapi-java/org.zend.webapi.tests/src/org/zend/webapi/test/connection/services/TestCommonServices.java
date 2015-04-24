package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestCommonServices extends AbstractTestServer {

	@Test
	public void testGetSystemInfo() throws WebApiException,
			MalformedURLException {
		initMock(handler.getSystemInfo(), "getSystemInfo", ResponseCode.OK);
		SystemInfo systemInfo = Configuration.getClient().getSystemInfo();
		DataUtils.checkValidSystemInfo(systemInfo);
	}

	@Test
	public void testClusterGetServerStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterGetServerStatus(), "clusterGetServerStatus",
				ResponseCode.OK);
		ServersList clusterServerStatus = Configuration.getClient()
				.clusterGetServerStatus();
		DataUtils.checkValidClusterServerStatus(clusterServerStatus);
	}

	@Test
	public void testClusterGetServerStatusId() throws WebApiException,
			MalformedURLException {
		initMock(handler.clusterGetServerStatus(), "clusterGetServerStatus",
				ResponseCode.OK);
		ServersList clusterServerStatus = Configuration.getClient()
				.clusterGetServerStatus("0");
		DataUtils.checkValidClusterServerStatus(clusterServerStatus);
	}

	@Test
	public void testRestartAllPhp() throws WebApiException,
			MalformedURLException {
		initMock(handler.restartPhp(), "restartPhp", ResponseCode.ACCEPTED);
		ServersList serversList = Configuration.getClient().restartPhp();
		DataUtils.checkValidServersList(serversList);
	}

	@Test
	public void testRestartAllPhpId() throws WebApiException,
			MalformedURLException {
		initMock(handler.restartPhp(), "restartPhp", ResponseCode.ACCEPTED);
		ServersList serversList = Configuration.getClient().restartPhp("0");
		DataUtils.checkValidServersList(serversList);
	}

}
