package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;


public interface IDependency extends IModelObject {
	
	String PHP = "php";
	String EXTENSION = "extension";
	String DIRECTIVE = "directive";
	String ZENDSERVER = "zendserver";
	String ZENDSERVERCOMPONENT = "zendservercomponent";
	String ZENDFRAMEWORK = "zendframework";
	
	String getType();
	
	String getName();
	
	String getEquals();
	
	String getMin();
	
	String getMax();
	
	List<String> getExclude();
	
	String getConflicts();
	
}
