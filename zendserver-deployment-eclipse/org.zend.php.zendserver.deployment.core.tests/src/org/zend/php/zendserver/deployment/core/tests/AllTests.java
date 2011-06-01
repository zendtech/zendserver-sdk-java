package org.zend.php.zendserver.deployment.core.tests;

import org.zend.php.zendserver.deployment.core.descriptor.AllCoreDescrTests;
import org.zend.php.zendserver.deployment.core.internal.descriptor.AllInternalDescriptorTests;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTest(AllCoreDescrTests.suite());
		suite.addTest(AllInternalDescriptorTests.suite());
		//$JUnit-END$
		return suite;
	}
}
