package org.zend.php.zendserver.deployment.core.descriptor;

import org.eclipse.core.resources.IProject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DeploymentDescriptor;


public class DeploymentDescriptorFactory {

	public static DeploymentDescriptor create(IProject project) {
		return new DeploymentDescriptor();
	}
	
}
