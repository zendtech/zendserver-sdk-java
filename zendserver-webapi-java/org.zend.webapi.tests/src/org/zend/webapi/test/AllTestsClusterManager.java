package org.zend.webapi.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.webapi.test.connection.TestClusterRequestParams;
import org.zend.webapi.test.connection.services.TestClusterServices;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllTestsCommon.class, TestClusterRequestParams.class,
		TestClusterServices.class })
public class AllTestsClusterManager {

}
