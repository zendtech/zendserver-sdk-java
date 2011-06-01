package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IPHPDependency extends IDependency {
	
	String getEquals();

	String getMin();

	String getMax();

	List<String> getExclude();
}
