package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IModelContainer extends IModelObject {

	void set(Feature key, int index, Object value);
	
	Object add(Feature key, Object o);
	
	void remove(Feature key, int index);
	
	List<Object> getChildren(Feature key);
	
	Feature[] getChildNames();
	
}
