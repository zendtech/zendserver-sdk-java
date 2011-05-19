package org.zend.sdk.test.sdkcli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.commands.TestCommandLine;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCommandTypes.class, TestMain.class,
		TestCommandLine.class, TestCreateProjectCommand.class })
public class AllCliTests {

}