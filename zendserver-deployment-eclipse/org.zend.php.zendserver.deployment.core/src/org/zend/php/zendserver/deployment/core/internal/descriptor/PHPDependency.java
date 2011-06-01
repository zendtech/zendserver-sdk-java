package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;

public class PHPDependency extends Dependency implements IPHPDependency {

	public PHPDependency() {
		this(null, null, null, null);
	}
	
	public PHPDependency(String equals, String min, String max, String[] excludes) {
		super(null, equals, min, max, excludes, null);
	}
}
