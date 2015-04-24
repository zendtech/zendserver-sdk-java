package org.zend.sdk.test.sdklib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zend.sdk.test.sdklib.library.TestIni;
import org.zend.sdk.test.sdklib.library.TestLibrary;
import org.zend.sdk.test.sdklib.library.TestZendProject;
import org.zend.sdk.test.sdklib.logger.TestLogger;
import org.zend.sdk.test.sdklib.manager.TestTargetsManager;
import org.zend.sdk.test.sdklib.mapping.TestDefaultMappingLoader;
import org.zend.sdk.test.sdklib.mapping.TestMappingModel;
import org.zend.sdk.test.sdklib.mapping.TestMappingValidator;
import org.zend.sdk.test.sdklib.monitor.TestZendIssue;
import org.zend.sdk.test.sdklib.monitor.TestZendMonitor;
import org.zend.sdk.test.sdklib.repository.TestRepositoryFactory;
import org.zend.sdk.test.sdklib.repository.TestRepositoryXsd;
import org.zend.sdk.test.sdklib.repository.TestVersionCompare;
import org.zend.sdk.test.sdklib.repository.TestVersionPattern;
import org.zend.sdk.test.sdklib.repository.site1.TestJarBasedRepositor;
import org.zend.sdk.test.sdklib.target.TestUserBasedTargetLoader;
import org.zend.sdk.test.sdklib.target.TestZendTarget;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestZendApplicationUpdate.class,
		TestZendApplicationDeploy.class, TestZendApplicationGetStatus.class,
		TestZendApplicationRedeploy.class, TestZendApplicationRemove.class,
		TestZendApplicationUpdate.class, TestZendCodeTracing.class,
		TestPackageBuilder.class, TestIni.class, TestLibrary.class,
		TestZendProject.class, TestLogger.class, TestTargetsManager.class,
		TestUserBasedTargetLoader.class, TestZendTarget.class,
		TestRepositoryXsd.class, TestJarBasedRepositor.class,
		TestVersionPattern.class, TestVersionCompare.class,
		TestDefaultMappingLoader.class, TestMappingModel.class,
		TestMappingValidator.class, TestZendIssue.class, TestZendMonitor.class,
		TestRepositoryFactory.class })
public class AllLibTests {

}
