package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;


public interface IDirectiveDependency extends IModelContainer {
	
	Feature DEPENDENCY_NAME = new Feature("name", null, String.class);
	Feature DEPENDENCY_EQUALS = new Feature("equals", null, String.class);
	Feature DEPENDENCY_MIN = new Feature("min", null, String.class);
	Feature DEPENDENCY_MAX = new Feature("max", null, String.class);
	Feature DEPENDENCY_EXCLUDE = new Feature("exclude", null, String.class);
	Feature DEPENDENCY_CONFLICTS = new Feature("conflicts", null, String.class);
	
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
