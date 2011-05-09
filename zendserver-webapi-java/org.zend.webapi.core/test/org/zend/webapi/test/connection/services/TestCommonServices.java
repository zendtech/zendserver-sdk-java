package org.zend.webapi.test.connection.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
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

	@Test
	public void testApplicationGetStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		ApplicationsList applicationGetStatus = Configuration.getClient()
				.applicationGetStatus();
		DataUtils.checkValidApplicationsList(applicationGetStatus);
	}

	@Test
	public void testApplicationGetStatusId() throws WebApiException,
			MalformedURLException {
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		ApplicationsList applicationGetStatus = Configuration.getClient()
				.applicationGetStatus("0");
		DataUtils.checkValidApplicationsList(applicationGetStatus);
	}

	@Test
	public void testApplicationDeploy() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						true, new HashMap<String, String>(), "test");
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy2() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						true);
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy3() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						new HashMap<String, String>());
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy4() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa");
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy5() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						"test");
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy6() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						new HashMap<String, String>(), "test");
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy7() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						true, new HashMap<String, String>());
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationDeploy8() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationDeploy(File.createTempFile("test", "aaa"), "aaaa",
						true, "bbbb");
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationUpdate() throws WebApiException, IOException {
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationUpdate(1, File.createTempFile("test", "aaa"), true,
						new HashMap<String, String>());
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationUpdateNullUserParam() throws WebApiException,
			IOException {
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationUpdate(1, File.createTempFile("test", "aaa"), true);
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationUpdateNullIgnoreAndUserParam()
			throws WebApiException, IOException {
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationUpdate(1, File.createTempFile("test", "aaa"));
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationUpdateNullIgnore() throws WebApiException,
			IOException {
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationUpdate(1, File.createTempFile("test", "aaa"),
						new HashMap<String, String>());
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationRemove() throws WebApiException, IOException {
		initMock(handler.applicationRemove(), "applicationRemove",
				ResponseCode.ACCEPTED);
		ApplicationInfo applicationInfo = Configuration.getClient()
				.applicationRemove(1);
		DataUtils.checkValidApplicationInfo(applicationInfo);
	}

	@Test
	public void testApplicationRedeploy() throws WebApiException, IOException {
		initMock(handler.applicationRedeploy(), "applicationRedeploy",
				ResponseCode.ACCEPTED);
		ApplicationsList applicationslist = Configuration.getClient()
				.applicationRedeploy(1, false, "test1", "test2");
		DataUtils.checkValidApplicationsList(applicationslist);
	}

	@Test
	public void testApplicationRedeployNoServers() throws WebApiException,
			IOException {
		initMock(handler.applicationRedeploy(), "applicationRedeploy",
				ResponseCode.ACCEPTED);
		ApplicationsList applicationslist = Configuration.getClient()
				.applicationRedeploy(1, false);
		DataUtils.checkValidApplicationsList(applicationslist);
	}

	@Test
	public void testApplicationRedeployNoServersAndIgnore()
			throws WebApiException, IOException {
		initMock(handler.applicationRedeploy(), "applicationRedeploy",
				ResponseCode.ACCEPTED);
		ApplicationsList applicationslist = Configuration.getClient()
				.applicationRedeploy(8);
		DataUtils.checkValidApplicationsList(applicationslist);
	}

	@Test
	public void testApplicationRedeployNoIgnore() throws WebApiException,
			IOException {
		initMock(handler.applicationRedeploy(), "applicationRedeploy",
				ResponseCode.ACCEPTED);
		ApplicationsList applicationslist = Configuration.getClient()
				.applicationRedeploy(1, "test1", "test2");
		DataUtils.checkValidApplicationsList(applicationslist);
	}

}
