package org.zend.webapi.test.connection.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationServers;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;
import org.zend.webapi.test.server.utils.ServerUtils;

public class TestDeploymentServices extends AbstractTestServer {

	public static final String DEPLOY_FOLDER = "deploy/";

	private int appId = 0;
	private ApplicationInfo appInfo;

	private void deployApplication() throws MalformedURLException,
			WebApiException, FileNotFoundException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://test.com";
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl);
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	private void deployParamApplication() throws MalformedURLException,
			WebApiException, FileNotFoundException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://testDeployParam.com";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, params);
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@After
	public void removeApplication() throws MalformedURLException,
			WebApiException {
		removeApplication(appId);
	}

	private void removeApplication(int id) throws MalformedURLException,
			WebApiException {
		if (isDeployed()) {
			initMock(handler.applicationRemove(), "applicationRemove",
					ResponseCode.ACCEPTED);
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationRemove(id);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

	private boolean isDeployed() throws MalformedURLException, WebApiException {
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		ApplicationStatus status = ApplicationStatus.STAGING;
		while (status != ApplicationStatus.DEPLOYED) {
			ApplicationsList applicationGetStatus = Configuration.getClient()
					.applicationGetStatus(String.valueOf(appId));
			List<ApplicationInfo> infos = applicationGetStatus
					.getApplicationsInfo();
			for (ApplicationInfo applicationInfo : infos) {
				if (applicationInfo.getId() == appId) {
					status = applicationInfo.getStatus();
				}
			}
		}
		return true;
	}

	@Test
	public void testApplicationGetStatus() throws WebApiException,
			MalformedURLException, FileNotFoundException {
		deployApplication();
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		ApplicationsList applicationGetStatus = Configuration.getClient()
				.applicationGetStatus();
		DataUtils.checkValidApplicationsList(applicationGetStatus);
	}

	@Test
	public void testApplicationGetStatusId() throws WebApiException,
			MalformedURLException, FileNotFoundException {
		deployApplication();
		initMock(handler.applicationGetStatus(), "applicationGetStatus",
				ResponseCode.OK);
		ApplicationsList applicationGetStatus = Configuration.getClient()
				.applicationGetStatus(String.valueOf(appId));
		DataUtils.checkValidApplicationsList(applicationGetStatus);
	}

	@Test
	public void testApplicationDeploy() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deployParams1.com";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, true, params, "appName",
							false, false);
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationDeploy2() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deploy2.com";
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, true);
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		}
	}

	@Test
	public void testApplicationDeploy5() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deploy5.com";
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, "deploy5");
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		}
	}

	@Test
	public void testApplicationDeploy6() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deploy6.com";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, params, "appName");
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationDeploy7() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deploy7.com";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, true, params);
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationDeploy8() throws WebApiException, IOException {
		initMock(handler.applicationDeploy(), "applicationDeploy",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			String baseUrl = "http://deploy8.com";
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationDeploy(new NamedInputStream(app), baseUrl, true, "deploy8");
			DataUtils.checkValidApplicationInfo(applicationInfo);
			appId = applicationInfo.getId();
			appInfo = applicationInfo;
		}
	}

	@Test
	public void testApplicationUpdate() throws WebApiException, IOException {
		deployParamApplication();
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationUpdate(appId, new NamedInputStream(app), true, params);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationUpdateNullUserParam() throws WebApiException,
			IOException {
		deployApplication();
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationUpdate(appId, new NamedInputStream(app), true);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationUpdateNullIgnoreAndUserParam()
			throws WebApiException, IOException {
		deployApplication();
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "test-1.0.0.zpk"));
		if (app.exists()) {
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationUpdate(appId, new NamedInputStream(app));
			DataUtils.checkValidApplicationInfo(applicationInfo);
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationUpdateNullIgnore() throws WebApiException,
			IOException {
		deployParamApplication();
		initMock(handler.applicationUpdate(), "applicationUpdate",
				ResponseCode.ACCEPTED);
		File app = new File(ServerUtils.createFileName(DEPLOY_FOLDER
				+ "testParam-1.0.0.zpk"));
		if (app.exists()) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("some_parameter", "test");
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationUpdate(appId, new NamedInputStream(app), params);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		} else {
			Assert.fail("Cannot find file: " + app.getAbsolutePath());
		}
	}

	@Test
	public void testApplicationRedeploy() throws WebApiException, IOException {
		deployApplication();
		if (isDeployed()) {
			initMock(handler.applicationRedeploy(), "applicationSynchronize",
					ResponseCode.ACCEPTED);
			ApplicationServers servers = appInfo.getServers();
			Assert.assertTrue(servers.getApplicationServers().size() > 0);
			int serverId = servers.getApplicationServers().get(0).getId();
			ApplicationInfo applicationInfo = Configuration
					.getClient()
					.applicationSynchronize(appId, false, String.valueOf(serverId));
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

	@Test
	public void testApplicationRedeployNoServers() throws WebApiException,
			IOException {
		deployApplication();
		if (isDeployed()) {
			initMock(handler.applicationRedeploy(), "applicationSynchronize",
					ResponseCode.ACCEPTED);
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationSynchronize(appId, false);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

	@Test
	public void testApplicationRedeployNoServersAndIgnore()
			throws WebApiException, IOException {
		deployApplication();
		if (isDeployed()) {
			initMock(handler.applicationRedeploy(), "applicationSynchronize",
					ResponseCode.ACCEPTED);
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationSynchronize(appId);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

	@Test
	public void testApplicationRedeployNoIgnore() throws WebApiException,
			IOException {
		deployApplication();
		if (isDeployed()) {
			initMock(handler.applicationRedeploy(), "applicationSynchronize",
					ResponseCode.ACCEPTED);
			ApplicationServers servers = appInfo.getServers();
			Assert.assertTrue(servers.getApplicationServers().size() > 0);
			int serverId = servers.getApplicationServers().get(0).getId();
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationSynchronize(appId, String.valueOf(serverId));
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

	@Test
	public void testApplicationRollback() throws WebApiException, IOException {
		deployApplication();
		if (isDeployed()) {
			initMock(handler.applicationRollback(), "applicationRollback",
					ResponseCode.ACCEPTED);
			ApplicationServers servers = appInfo.getServers();
			Assert.assertTrue(servers.getApplicationServers().size() > 0);
			ApplicationInfo applicationInfo = Configuration.getClient()
					.applicationRollback(appId);
			DataUtils.checkValidApplicationInfo(applicationInfo);
		}
	}

}
