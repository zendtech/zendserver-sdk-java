package org.zend.sdk.test.sdklib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdklib.library.TestIni;
import org.zend.sdk.test.sdklib.library.TestLibrary;
import org.zend.sdk.test.sdklib.library.TestStatusCode;
import org.zend.sdk.test.sdklib.library.TestZendProject;
import org.zend.sdk.test.sdklib.logger.TestLogger;
import org.zend.sdk.test.sdklib.manager.TestTargetsManager;
import org.zend.sdk.test.sdklib.target.TestUserBasedTargetLoader;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestIni.class, TestLibrary.class, TestStatusCode.class,
		TestZendProject.class, TestLogger.class, TestTargetsManager.class,
		TestUserBasedTargetLoader.class })
public class AllLibTests {

}
