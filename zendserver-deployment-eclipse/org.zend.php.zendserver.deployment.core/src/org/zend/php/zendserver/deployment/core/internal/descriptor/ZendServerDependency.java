package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;

public class ZendServerDependency extends Dependency implements IZendServerDependency {

	public ZendServerDependency() {
		this(null, null, null, null);
	}

	public ZendServerDependency(String equals, String min, String max, String[] excludes) {
		super(null, equals, min, max, excludes, null);
	}
}
