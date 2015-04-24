package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestZendApplicationGetStatus extends AbstractWebApiTest {

	@Test
	public void getStatusSuccess() throws WebApiException, IOException {
		setGetStatusSuccessCall();
		ApplicationsList list = application.getStatus("0");
		assertNotNull(list);
		assertTrue(list.getApplicationsInfo().size() == 2);
	}

	@Test
	public void getStatusFailed() throws WebApiException, IOException {
		setGetStatusFailedCall();
		ApplicationsList list = application.getStatus("0");
		assertNull(list);
	}

	private void setGetStatusSuccessCall() throws WebApiException, IOException {
		when(client.applicationGetStatus()).thenReturn(
				(ApplicationsList) getResponseData("applicationGetStatus",
						IResponseData.ResponseType.APPLICATIONS_LIST));
	}

	private void setGetStatusFailedCall() throws WebApiException, IOException {
		when(client.applicationGetStatus()).thenThrow(
				new SignatureException("testError"));
	}
}
