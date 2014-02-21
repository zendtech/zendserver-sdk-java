package org.zend.php.zendserver.deployment.core.internal.descriptor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllInternalDescriptorTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllInternalDescriptorTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(DescriptorNotificationsTests.class);
		suite.addTestSuite(ModelSerializerWriteTests.class);
		suite.addTestSuite(ModelSerializerReadTests.class);
		//$JUnit-END$
		return suite;
	}

}
