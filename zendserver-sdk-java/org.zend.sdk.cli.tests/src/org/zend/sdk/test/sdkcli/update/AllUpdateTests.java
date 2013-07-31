package org.zend.sdk.test.sdkcli.update;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.update.parser.TestAddEntry;
import org.zend.sdk.test.sdkcli.update.parser.TestDelta;
import org.zend.sdk.test.sdkcli.update.parser.TestDeltaParser;
import org.zend.sdk.test.sdkcli.update.parser.TestRange;
import org.zend.sdk.test.sdkcli.update.parser.TestRemoveEntry;
import org.zend.sdk.test.sdkcli.update.parser.TestSdkVersion;
import org.zend.sdk.test.sdkcli.update.parser.TestVersion;
import org.zend.sdk.test.sdkcli.update.parser.TestVersionParser;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestAddEntry.class, TestRange.class,
		TestSdkVersion.class, TestVersion.class, TestAddEntry.class,
		TestRemoveEntry.class, TestDelta.class, TestDeltaParser.class,
		TestVersionParser.class, TestUpdateManager.class })
public class AllUpdateTests {

}