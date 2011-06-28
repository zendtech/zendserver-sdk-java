package org.zend.php.zendserver.deployment.core.descriptor;


public interface IVariable extends IModelObject {
	
	String getValue();
	
	void setValue(String value);

	String getName();
	
	void setName(String name);

}
