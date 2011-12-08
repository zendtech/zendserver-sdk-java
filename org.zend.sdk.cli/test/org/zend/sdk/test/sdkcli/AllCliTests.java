package org.zend.sdk.test.sdkcli;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdkcli.commands.TestAddRepositoryCommand;
import org.zend.sdk.test.sdkcli.commands.TestAddTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestCommandLine;
import org.zend.sdk.test.sdkcli.commands.TestCreatePackageCommand;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;
import org.zend.sdk.test.sdkcli.commands.TestDeployApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestDetectTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestListApplicationsCommand;
import org.zend.sdk.test.sdkcli.commands.TestListRepositoriesCommand;
import org.zend.sdk.test.sdkcli.commands.TestListTargetsCommand;
import org.zend.sdk.test.sdkcli.commands.TestRedeployApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestRemoveApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestRemoveRepositoryCommand;
import org.zend.sdk.test.sdkcli.commands.TestRemoveTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestUpdateApplicationCommand;
import org.zend.sdk.test.sdkcli.commands.TestUpdateProjectCommand;
import org.zend.sdk.test.sdkcli.commands.TestUpdateTargetCommand;
import org.zend.sdk.test.sdkcli.commands.TestUsageCommand;
import org.zend.sdk.test.sdkcli.update.AllUpdateTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCommandTypes.class, TestEnvironmetUtils.class,
		TestMain.class, TestCommandLine.class, TestCreatePackageCommand.class,
		TestCreateProjectCommand.class, TestAddTargetCommand.class,
		TestRemoveTargetCommand.class, TestDeployApplicationCommand.class,
		TestDetectTargetCommand.class, TestListApplicationsCommand.class,
		TestListTargetsCommand.class, TestRedeployApplicationCommand.class,
		TestRemoveApplicationCommand.class, TestUpdateApplicationCommand.class,
		TestUpdateProjectCommand.class, TestUpdateTargetCommand.class,
		TestAddRepositoryCommand.class, TestListRepositoriesCommand.class,
		TestRemoveRepositoryCommand.class, TestUsageCommand.class,
		AllUpdateTests.class })
public class AllCliTests {

}