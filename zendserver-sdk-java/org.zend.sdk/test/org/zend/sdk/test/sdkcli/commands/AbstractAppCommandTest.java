package org.zend.sdk.test.sdkcli.commands;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.mockito.Mockito;
import org.restlet.ext.xml.DomRepresentation;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.ZendApplication;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.connection.data.DataDigster;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.test.server.utils.ServerUtils;

public class AbstractAppCommandTest extends AbstractTest {

	private static final String FOLDER = "test/config/apps/";

	protected ZendApplication application;
	protected WebApiClient client;

	@Before
	public void startup() throws MalformedURLException {
		application = spy(new ZendApplication());
		client = Mockito.mock(WebApiClient.class);
		doReturn(client).when(application).getClient(anyString());
	}

	protected IResponseData getResponseData(String fileName,
			IResponseData.ResponseType type) throws IOException {
		DomRepresentation representation = ServerUtils
				.readDomRepresentation(FOLDER + fileName + ".xml");
		final DataDigster dataDigster = new DataDigster(type, representation);
		dataDigster.digest();
		return dataDigster.getResponseData();
	}

}
