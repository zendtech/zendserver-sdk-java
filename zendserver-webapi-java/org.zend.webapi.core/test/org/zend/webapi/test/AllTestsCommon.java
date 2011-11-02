package org.zend.webapi.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.webapi.test.connection.TestCredentials;
import org.zend.webapi.test.connection.TestRequest;
import org.zend.webapi.test.connection.TestResponse;
import org.zend.webapi.test.connection.TestSignatrue;
import org.zend.webapi.test.connection.data.TestDataDigester;
import org.zend.webapi.test.connection.data.TestValues;
import org.zend.webapi.test.connection.services.TestDeploymentServices;
import org.zend.webapi.test.connection.services.TestServerConfiguration;
import org.zend.webapi.test.core.TestClientConfiguration;
import org.zend.webapi.test.core.TestServiceDispatcher;
import org.zend.webapi.test.core.TestUtils;
import org.zend.webapi.test.core.TestWebApiExceptions;
import org.zend.webapi.test.core.TestWebApiMethodType;
import org.zend.webapi.test.core.progress.TestStatusCode;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCredentials.class, TestRequest.class,
		TestResponse.class, TestSignatrue.class, TestDataDigester.class,
		TestValues.class, TestDeploymentServices.class,
		TestServerConfiguration.class, TestClientConfiguration.class,
		TestServiceDispatcher.class, TestWebApiExceptions.class,
		TestWebApiMethodType.class, TestStatusCode.class, TestValues.class,
		TestClientConfiguration.class, TestUtils.class,
		TestWebApiExceptions.class })
public class AllTestsCommon {

}
