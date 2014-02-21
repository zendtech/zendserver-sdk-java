package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;


public interface IZendComponentDependency extends IModelContainer {
	
	String getName();
	
	void setName(String name);
	
	String getEquals();
	
	void setEquals(String equals);
	
	String getMin();
	
	void setMin(String min);
	
	String getMax();
	
	void setMax(String max);
	
	List<String> getExclude();
	
	String getConflicts();
	
	void setConflicts(String conflicts);
	
}
