package org.zend.webapi.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.webapi.test.connection.TestCommonRequestParams;
import org.zend.webapi.test.connection.TestCredentials;
import org.zend.webapi.test.connection.TestResponse;
import org.zend.webapi.test.connection.TestSignatrue;
import org.zend.webapi.test.connection.data.TestDataDigester;
import org.zend.webapi.test.connection.data.TestValues;
import org.zend.webapi.test.connection.services.TestServerConfiguration;
import org.zend.webapi.test.connection.services.TestZendServerServices;
import org.zend.webapi.test.core.TestClientConfiguration;
import org.zend.webapi.test.core.TestServiceDispatcher;
import org.zend.webapi.test.core.TestUtils;
import org.zend.webapi.test.core.TestWebApiExceptions;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCredentials.class, TestCommonRequestParams.class,
		TestResponse.class, TestSignatrue.class, TestDataDigester.class,
		TestValues.class, TestServerConfiguration.class,
		TestZendServerServices.class, TestClientConfiguration.class,
		TestServiceDispatcher.class, TestWebApiExceptions.class,
		TestValues.class, TestClientConfiguration.class, TestUtils.class,
		TestWebApiExceptions.class })
public class AllTestsZendServer {

}
