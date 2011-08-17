package org.zend.sdk.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.AllCliTests;
import org.zend.sdk.test.sdklib.AllLibTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AllCliTests.class, AllLibTests.class })
public class AllTests {

}
