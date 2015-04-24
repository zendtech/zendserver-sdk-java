package org.zend.webapi.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.webapi.test.connection.TestCommonRequestParams;
import org.zend.webapi.test.connection.services.TestZendServerServices;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTestsCommon.class, TestCommonRequestParams.class,
		TestZendServerServices.class })
public class AllTestsZendServer {

}
