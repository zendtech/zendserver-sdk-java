package org.zend.php.zendserver.deployment.core.descriptor;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IDescriptorChangeListener {
	
	public static final int SET = 1;
	public static final int ADD = 2;
	public static final int REMOVE = 4;

	void descriptorChanged(IModelObject target, Feature feature, int type);
	
}
