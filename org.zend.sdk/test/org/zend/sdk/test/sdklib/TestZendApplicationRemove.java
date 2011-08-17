package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestZendApplicationRemove extends AbstractWebApiTest {

	@Test
	public void removeSuccess() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.remove("0", "0");
		assertNotNull(info);
		assertEquals("Home CMS", info.getAppName());
	}

	@Test
	public void updateInvalidAppId() throws WebApiException, IOException {
		setUpdateSuccessCall();
		ApplicationInfo info = application.remove("0", "notANumber");
		assertNull(info);
	}

	@Test
	public void updateConnectionFailed() throws WebApiException, IOException {
		setUpdateFailedCall();
		ApplicationInfo info = application.remove("0", "0");
		assertNull(info);
	}

	private void setUpdateSuccessCall() throws WebApiException, IOException {
		when(
				client.applicationRemove(anyInt())).thenReturn(
				(ApplicationInfo) getResponseData("applicationRemove",
						IResponseData.ResponseType.APPLICATION_INFO));
	}
	
	private void setUpdateFailedCall() throws WebApiException, IOException {
		when(
			client.applicationRemove(anyInt())).thenThrow(
				new SignatureException("testError"));
	}

}
