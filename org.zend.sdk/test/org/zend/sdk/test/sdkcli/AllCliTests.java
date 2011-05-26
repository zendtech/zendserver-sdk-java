package org.zend.sdk.test.sdkcli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.commands.TestCommandLine;
import org.zend.sdk.test.sdkcli.commands.TestCreatePackageCommand;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;
import org.zend.sdk.test.sdkcli.commands.TestCreateTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestDeleteTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestDeployApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestDetectTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestListApplicationsCommand;
import org.zend.sdk.test.sdkcli.commands.TestListTargetsCommand;
import org.zend.sdk.test.sdkcli.commands.TestRedeployApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestRemoveApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestUpdateApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestUpdateProjectCommand;
import org.zend.sdk.test.sdkcli.commands.TestUsageCommand;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCommandTypes.class, TestEnvironmetUtils.class,
		TestMain.class, TestCommandLine.class, TestCreatePackageCommand.class,
		TestCreateProjectCommand.class, TestCreateTargetCommand.class,
		TestDeleteTargetCommand.class, TestDeployApplicationCommand.class,
		TestDetectTargetCommand.class, TestListApplicationsCommand.class,
		TestListTargetsCommand.class, TestRedeployApplicationCommand.class,
		TestRemoveApplicationCommand.class, TestUpdateApplicationCommand.class,
		TestUpdateProjectCommand.class, TestUsageCommand.class })
public class AllCliTests {

}