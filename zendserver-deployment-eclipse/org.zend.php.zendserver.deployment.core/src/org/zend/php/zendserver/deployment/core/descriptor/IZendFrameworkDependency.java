package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;


public interface IZendFrameworkDependency extends IModelContainer {
	
	String getEquals();
	
	void setEquals(String equals);
	
	String getMin();
	
	void setMin(String min);
	
	String getMax();
	
	void setMax(String max);
	
	List<String> getExclude();
}
