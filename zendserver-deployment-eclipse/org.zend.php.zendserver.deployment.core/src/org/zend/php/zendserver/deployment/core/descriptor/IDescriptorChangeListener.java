package org.zend.php.zendserver.deployment.core.descriptor;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IDescriptorChangeListener {

	void descriptorChanged(IModelObject target, Feature feature);
	
}
