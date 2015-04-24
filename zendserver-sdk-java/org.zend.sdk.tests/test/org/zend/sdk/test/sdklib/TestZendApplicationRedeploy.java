package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestZendApplicationRedeploy extends AbstractWebApiTest {

	@Test
	public void testRedeploySuccess() throws WebApiException, IOException {
		setSuccessCall();
		ApplicationInfo info = application.redeploy("0", "0", null, false);
		assertNotNull(info);
		assertEquals("Home CMS", info.getAppName());
	}
	
	@Test
	public void testRedeployInvalidAppId() throws WebApiException, IOException {
		setSuccessCall();
		ApplicationInfo info = application.redeploy("0", "notANumber", null, false);
		assertNull(info);
	}

	@Test
	public void connectionFailed() throws WebApiException, IOException {
		setFailedCall();
		ApplicationInfo info = application.redeploy("0", "0", null, false);
		assertNull(info);
	}

	private void setSuccessCall() throws WebApiException, IOException {
		when(
			client.applicationSynchronize(anyInt(), anyBoolean(), (String[])anyVararg())).thenReturn(
				(ApplicationInfo) getResponseData("applicationRedeploy",
						IResponseData.ResponseType.APPLICATION_INFO));
	}
	
	private void setFailedCall() throws WebApiException, IOException {
		when(
			client.applicationSynchronize(anyInt(), anyBoolean(), (String[])anyVararg())).thenThrow(
				new SignatureException("testError"));
	}

}
