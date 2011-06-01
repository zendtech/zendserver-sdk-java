package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IZendServerComponentDependency;

public class ZendServerComponentDependency extends Dependency implements IZendServerComponentDependency {

	public ZendServerComponentDependency() {
		this(null, null, null, null, null, null);
	}

	public ZendServerComponentDependency(String name, String equals, String min, String max, String[] excludes, String conflicts) {
		super(name, equals, min, max, excludes, conflicts);
	}
}
