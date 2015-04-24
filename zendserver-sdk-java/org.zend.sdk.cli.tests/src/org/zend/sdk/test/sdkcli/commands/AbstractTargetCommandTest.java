package org.zend.sdk.test.sdkcli.commands;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

public class AbstractTargetCommandTest extends AbstractTest {

	private ITargetLoader loader;
	protected TargetsManager manager;

	@Before
	public void startup() {
		loader = new UserBasedTargetLoader(file);
		manager = spy(new TargetsManager(loader));
	}

	protected IZendTarget getTarget() throws WebApiException,
			LicenseExpiredException {
		IZendTarget target = null;
		try {
			target = spy(new ZendTarget("dev4", new URL("http://localhost"),
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
