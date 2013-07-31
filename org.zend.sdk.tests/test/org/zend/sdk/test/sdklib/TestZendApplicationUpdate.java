package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestZendApplicationUpdate extends AbstractWebApiTest {

	@Test
	public void updatePackageSuccess() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.update(FOLDER + "test-1.0.0.zpk", "0", "0",
				(String) null, false);
		assertNotNull(info);
		assertEquals("Home CMS", info.getAppName());
	}

	@Test
	public void updateProjectSuccess() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.update(FOLDER + "Project1", "0", "0", (String) null,
				false);
		assertNotNull(info);
		assertEquals("Home CMS", info.getAppName());
	}

	@Test
	public void updateInvalidPath() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.update("invalid_path", "0", "0", FOLDER
				+ "userParams.properties", false);
		assertNull(info);
	}

	@Test
	public void updateUserParams() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.update(FOLDER + "test-1.0.0.zpk", "0", "0", FOLDER
				+ "userParams.properties", false);
		assertNotNull(info);
		assertEquals("Home CMS", info.getAppName());
	}

	@Test
	public void updateConnectionFailed() throws WebApiException, IOException {
		setUpdateFailedCall();
		ApplicationInfo info = application.update(FOLDER + "test-1.0.0.zpk", "0", "0",
				(String) null, false);
		assertNull(info);
	}

	private void setUpdateSuccessCall() throws WebApiException, IOException {
		when(
				client.applicationUpdate(anyInt(), any(NamedInputStream.class), anyBoolean(), any(Map.class))).thenReturn(
				(ApplicationInfo) getResponseData("applicationUpdate",
						IResponseData.ResponseType.APPLICATION_INFO));
	}

	private void setUpdateFailedCall() throws WebApiException, IOException {
		when(
			client.applicationUpdate(anyInt(), any(NamedInputStream.class), anyBoolean(), any(Map.class))).thenThrow(
				new SignatureException("testError"));
	}

}
