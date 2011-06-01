package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;

public class ZendFrameworkDependency extends Dependency implements IZendFrameworkDependency {

	public ZendFrameworkDependency() {
		this(null, null, null, null);
	}
	
	public ZendFrameworkDependency(String equals, String min, String max, String[] excludes) {
		super(null, equals, min, max, excludes, null);
	}
}
