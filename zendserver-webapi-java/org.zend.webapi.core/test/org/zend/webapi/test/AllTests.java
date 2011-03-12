package org.zend.webapi.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.webapi.test.connection.TestConfiguration;
import org.zend.webapi.test.connection.TestRequestParams;
import org.zend.webapi.test.connection.TestServiceMethods;
import org.zend.webapi.test.connection.data.TestDataDigester;
import org.zend.webapi.test.signature.SignatureTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SignatureTest.class, TestDataDigester.class,
		TestRequestParams.class, TestServiceMethods.class,
		TestConfiguration.class })
public class AllTests {

	// empty suite class

}
