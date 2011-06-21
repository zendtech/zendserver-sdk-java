package org.zend.php.zendserver.deployment.core.descriptor;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public interface IVariable extends IModelObject {

	Feature NAME = new Feature("name", null, String.class);
	Feature VALUE = new Feature("value", null, String.class);
	
	String getValue();
	
	void setValue(String value);

	String getName();
	
	void setName(String name);

}
