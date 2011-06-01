package org.zend.php.zendserver.deployment.core.descriptor;

public interface IDirectiveDependency extends IDependency {

	String getName();

	String getEquals();

	String getMin();

	String getMax();
}
