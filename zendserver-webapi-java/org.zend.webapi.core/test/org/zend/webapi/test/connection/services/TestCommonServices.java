package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.LicenseInfo;
import org.zend.webapi.core.connection.data.MessageList;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.SystemStatus;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;

public class TestCommonServices extends AbstractTestServer {

	@Test
	public void testGetSystemInfo() throws WebApiException,
			MalformedURLException {
		initMock(handler.getSystemInfo(), "getSystemInfo", ResponseCode.OK);
		SystemInfo systemInfo = Configuration.getClient().getSystemInfo();
		checkValidSystemInfo(systemInfo);
	}

	@Test
	public void testRestartAllPhp() throws WebApiException,
			MalformedURLException {
		initMock(handler.restartPhp(), "restartPhp", ResponseCode.ACCEPTED);
		ServersList serversList = Configuration.getClient().restartPhp();
		checkValidServersList(serversList);
	}

	protected void checkValidServersList(ServersList serversList) {
		Assert.assertNotNull(serversList);
		List<ServerInfo> servers = serversList.getServerInfo();
		for (ServerInfo serverInfo : servers) {
			checkValidServerInfo(serverInfo);
		}
	}

	protected void checkValidClusterServerStatus(ServersList clusterServerStatus) {
		Assert.assertNotNull(clusterServerStatus);
		Assert.assertEquals(IResponseData.ResponseType.SERVERS_LIST,
				clusterServerStatus.getType());
		List<ServerInfo> servers = clusterServerStatus.getServerInfo();
		for (ServerInfo serverInfo : servers) {
			checkValidServerInfo(serverInfo);
		}
	}

	protected void checkValidServerInfo(ServerInfo serverInfo) {
		Assert.assertNotNull(serverInfo);
		Assert.assertTrue(Integer.valueOf(serverInfo.getId()) instanceof Integer);
		Assert.assertNotNull(serverInfo.getAddress());
		Assert.assertNotNull(serverInfo.getName());
		Assert.assertNotSame(ServerStatus.UNKNOWN, serverInfo.getStatus());
		checkValidMessageList(serverInfo.getMessageList());
	}

	protected void checkValidSystemInfo(SystemInfo systemInfo) {
		Assert.assertNotNull(systemInfo);
		checkValidEdition(systemInfo.getEdition());
		checkValidLicenceInfo(systemInfo.getLicenseInfo());
		checkValidLicenceInfo(systemInfo.getManagerLicenseInfo());
		Assert.assertNotNull(systemInfo.getOperatingSystem());
		Assert.assertNotNull(systemInfo.getPhpVersion());
		checkValidSystemStatus(systemInfo.getStatus());
		checkValidApiVersions(systemInfo.getSupportedApiVersions());
		Assert.assertNotNull(systemInfo.getVersion());
		checkValidMessageList(systemInfo.getMessageList());
	}

	protected void checkValidApiVersions(
			List<WebApiVersion> supportedApiVersions) {
		Assert.assertNotNull(supportedApiVersions);
		for (WebApiVersion webApiVersion : supportedApiVersions) {
			Assert.assertNotNull(webApiVersion.getFullName());
			Assert.assertNotNull(webApiVersion.getVersionName());
		}
	}

	protected void checkValidSystemStatus(SystemStatus status) {
		Assert.assertNotSame(SystemStatus.UNKNOWN,
				SystemStatus.byName(status.getTitle()));
	}

	protected void checkValidLicenceInfo(LicenseInfo licenseInfo) {
		Assert.assertNotNull(licenseInfo);
		Assert.assertNotNull(licenseInfo.getOrderNumber());
		Assert.assertTrue(licenseInfo.getServerLimit() >= 0);
	}

	protected void checkValidEdition(SystemEdition edition) {
		Assert.assertNotNull(edition);
		boolean isCorrect = edition == SystemEdition.ZEND_SERVER
				|| edition == SystemEdition.ZEND_SERVER_CLUSER_MANAGER
				|| edition == SystemEdition.ZEND_SERVER_COMMUNITY_EDITION;
		Assert.assertTrue(isCorrect);
	}

	protected void checkValidMessageList(MessageList messageList) {
		Assert.assertNotNull(messageList);
		List<String> errors = messageList.getError();
		if (errors != null) {
			for (String error : errors) {
				Assert.assertNotNull(error);
			}
		}
		List<String> infoList = messageList.getInfo();
		if (infoList != null) {
			for (String info : infoList) {
				Assert.assertNotNull(info);
			}
		}
		List<String> warnings = messageList.getWarning();
		if (warnings != null) {
			for (String warining : warnings) {
				Assert.assertNotNull(warining);
			}
		}
	}

}
