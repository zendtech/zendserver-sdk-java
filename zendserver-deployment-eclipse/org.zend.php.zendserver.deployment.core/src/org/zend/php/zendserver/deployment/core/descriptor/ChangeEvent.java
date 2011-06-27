package org.zend.php.zendserver.deployment.core.descriptor;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class ChangeEvent {
	public IModelObject target;
	public Feature feature;
	public int type;
	public Object newValue;
	public Object oldValue;

	public ChangeEvent(IModelObject target, Feature feature, int type, Object newValue, Object oldValue) {
		this.target = target;
		this.feature = feature;
		this.type = type;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
}