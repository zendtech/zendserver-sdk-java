package org.zend.php.zendserver.deployment.core.descriptor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCoreDescrTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllCoreDescrTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(DescriptorContainerManagerTest.class);
		//$JUnit-END$
		return suite;
	}
}
