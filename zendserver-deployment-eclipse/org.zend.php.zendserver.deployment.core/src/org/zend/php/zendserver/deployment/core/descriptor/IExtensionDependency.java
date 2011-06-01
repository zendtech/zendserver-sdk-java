package org.zend.php.zendserver.deployment.core.descriptor;

import java.util.List;

public interface IExtensionDependency extends IDependency {

	String getName();

	String getEquals();

	String getMin();

	String getMax();

	List<String> getExclude();

	String getConflicts();
}
