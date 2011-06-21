package org.zend.php.zendserver.deployment.core.internal.descriptor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;

public class Tester {

	@Test
	public void test() {
		DeploymentDescriptor dd = new DeploymentDescriptor();
		
		
		dd.setName("newName");
		dd.set(IDeploymentDescriptor.NAME, "newName");
		assertEquals("abc", dd.getName());
	}

}
