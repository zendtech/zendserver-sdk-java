package org.zend.sdk.test.sdkcli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.commands.TestCommandLine;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;
import org.zend.sdk.test.sdkcli.commands.TestCreateTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestDetectTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestListApplicationsCommand;
import org.zend.sdk.test.sdkcli.commands.TestListTargetsCommand;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCommandTypes.class, TestEnvironmetUtils.class,
		TestMain.class, TestCommandLine.class, TestCreateProjectCommand.class,
		TestCreateTargetCommand.class, TestDetectTargetCommand.class,
		TestListApplicationsCommand.class, TestListTargetsCommand.class })
public class AllCliTests {

}