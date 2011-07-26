package org.zend.php.zendserver.deployment.core.descriptor;



public interface IDirectiveDependency extends IModelContainer {
	
	String getName();
	
	void setName(String name);
	
	String getEquals();
	
	void setEquals(String equals);
	
	String getMin();
	
	void setMin(String min);
	
	String getMax();
	
	void setMax(String max);
}
