package org.zend.sdk.test.sdkcli.commands;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.mockito.Mockito;
import org.restlet.ext.xml.DomRepresentation;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.DataDigster;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.test.server.utils.ServerUtils;

public class AbstractWebapiCommandTest extends AbstractTest {

	public static final String FOLDER = "test/config/apps/";

	protected ZendApplication application;
	protected ZendCodeTracing codeTracing;
	protected WebApiClient client;
	private ITargetLoader loader;
	protected TargetsManager manager;

	@Before
	public void startup() throws MalformedURLException {
		application = spy(new ZendApplication(new UserBasedTargetLoader()));
		client = Mockito.mock(WebApiClient.class);
		doReturn(client).when(application).getClient(anyString());
		codeTracing = spy(new ZendCodeTracing("targetId",
				new UserBasedTargetLoader()));
		doReturn(client).when(codeTracing).getClient(anyString());
		loader = new UserBasedTargetLoader(file);
		manager = spy(new TargetsManager(loader));
	}

	protected IResponseData getResponseData(String fileName,
			IResponseData.ResponseType type) throws IOException {
		DomRepresentation representation = ServerUtils
				.readDomRepresentation(FOLDER + fileName + ".xml");
		final DataDigster dataDigster = new DataDigster(type, representation);
		dataDigster.digest();
		return dataDigster.getResponseData();
	}

	protected IZendTarget getTarget() throws WebApiException,
			LicenseExpiredException {
		IZendTarget target = null;
		try {
			target = spy(new ZendTarget("0", new URL("http://localhost"),
					"mykey", "123456"));
			doReturn(true).when(target).connect(WebApiVersion.V1_3,
					ServerType.ZEND_SERVER);
			doReturn(true).when(target).connect();
		} catch (MalformedURLException e) {
			// ignore
		}
		return target;
	}

}
