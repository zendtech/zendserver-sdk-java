package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.RepositoryFactory;
import org.zend.sdklib.repository.site.Application;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestZendApplicationDeploy extends AbstractWebApiTest {

	@Test
	public void deployPackageSuccess() throws WebApiException, IOException {
		setDeploySuccessCall();
		ApplicationInfo info = application.deploy(FOLDER + "test-1.0.0.zpk",
				"aaa", "1_0", (String) null, null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
	}

	@Test
	public void deployProjectSuccess() throws WebApiException, IOException {
		setDeploySuccessCall();
		ApplicationInfo info = application
				.deploy(FOLDER + "Project1", "http://myhost/aaa", "0",
						(String) null, null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
	}

	@Test
	public void deployInvalidPath() throws WebApiException, IOException {
		setDeploySuccessCall();
		ApplicationInfo info = application
				.deploy("invalid_path", "http://myhost/aaa", "0",
						(String) null, null, null, null, null);
		assertNull(info);
	}

	@Test
	public void deployUserParams() throws WebApiException, IOException {
		setDeploySuccessCall();
		ApplicationInfo info = application.deploy(FOLDER + "test-1.0.0.zpk",
				"http://myhost/aaa", "0", FOLDER + "userParams.properties",
				null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
	}

	@Test
	public void deployUserParamsMap() throws WebApiException, IOException {
		setDeploySuccessCall();
		Map<String, String> userParams = new HashMap<String, String>();
		userParams.put("key", "value");
		ApplicationInfo info = application.deploy(FOLDER + "test-1.0.0.zpk",
				"http://myhost/aaa", "0", userParams, null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
	}

	@Test
	public void deployConnectionFailed() throws WebApiException, IOException {
		setDeployFailedCall();
		ApplicationInfo info = application.deploy(FOLDER + "test-1.0.0.zpk",
				"http://myhost/aaa", "0", FOLDER + "userParams.properties",
				null, null, null, null);
		assertNull(info);
	}

	@Test
	public void deployFromRepository() throws WebApiException, IOException,
			SdkException {
		File repositoryFolder = new File(FOLDER + "repository");
		String reposiotryUrl = "file:/" + repositoryFolder.getAbsolutePath();
		IRepository repository = RepositoryFactory.createRepository(
				reposiotryUrl, "testRepo");
		Application app = repository.getSite().getApplication().get(0);
		setDeploySuccessCall();
		ApplicationInfo info = application.deploy(repository.getPackage(app),
				app,
				"aaa", "1_0", (String) null, null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
		info = application.deploy(repository.getPackage(app), app, "aaa",
				"1_0", new HashMap<String, String>(), null, null, null, null);
		assertNotNull(info);
		assertEquals("test", info.getAppName());
	}

	@Test
	public void deployFromRepositoryNullStream() throws WebApiException,
			IOException, SdkException {
		File repositoryFolder = new File(FOLDER + "repository");
		String reposiotryUrl = "file:/" + repositoryFolder.getAbsolutePath();
		IRepository repository = RepositoryFactory.createRepository(
				reposiotryUrl, "testRepo");
		Application app = repository.getSite().getApplication().get(0);
		setDeploySuccessCall();
		ApplicationInfo info = application.deploy(null, app, "aaa", "1_0",
				(String) null, null, null, null, null);
		assertNull(info);
		info = application.deploy(null, app, "aaa", "1_0",
				new HashMap<String, String>(), null, null, null, null);
		assertNull(info);
	}

	private void setDeploySuccessCall() throws WebApiException, IOException {
		when(
				client.applicationDeploy(any(NamedInputStream.class), anyString(),
						anyBoolean(), any(Map.class), anyString(),
						anyBoolean(), anyBoolean())).thenReturn(
				(ApplicationInfo) getResponseData("applicationDeploy",
						IResponseData.ResponseType.APPLICATION_INFO));
	}

	private void setDeployFailedCall() throws WebApiException, IOException {
		when(
				client.applicationDeploy(any(NamedInputStream.class), anyString(),
						anyBoolean(), any(Map.class), anyString(),
						anyBoolean(), anyBoolean())).thenThrow(
				new SignatureException("testError"));
	}
}
