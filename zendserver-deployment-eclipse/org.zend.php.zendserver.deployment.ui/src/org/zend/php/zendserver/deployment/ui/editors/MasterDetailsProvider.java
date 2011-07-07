package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;

public interface MasterDetailsProvider {

	String getDescription();

	Object[] doGetElements(Object input);
	
	Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block);

	Class getType();

	Object doGetParent(Object element);
	
}
