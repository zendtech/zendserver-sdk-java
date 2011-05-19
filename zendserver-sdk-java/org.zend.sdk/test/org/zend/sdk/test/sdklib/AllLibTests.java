package org.zend.sdk.test.sdklib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdklib.library.TestLibrary;
import org.zend.sdk.test.sdklib.library.TestStatusCode;
import org.zend.sdk.test.sdklib.library.TestZendProject;
import org.zend.sdk.test.sdklib.target.TestUserBasedTargetLoader;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestLibrary.class, TestStatusCode.class,
		TestZendProject.class, TestLibrary.class,
		TestUserBasedTargetLoader.class, TestUserBasedTargetLoader.class })
public class AllLibTests {

}
