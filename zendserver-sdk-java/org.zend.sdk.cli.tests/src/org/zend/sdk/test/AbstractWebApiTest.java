package org.zend.sdk.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.mockito.Mockito;
import org.restlet.ext.xml.DomRepresentation;
import org.zend.sdkcli.internal.mapping.CliMappingLoader;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.connection.data.DataDigster;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.test.server.utils.ServerUtils;

public class AbstractWebApiTest extends AbstractTest {

	protected ZendApplication application;
	protected WebApiClient client;

	@Before
	public void startup() throws MalformedURLException {
		application = spy(new ZendApplication(new UserBasedTargetLoader(),
				new CliMappingLoader()));
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
