package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;

public class DirectiveDependency extends Dependency implements IDirectiveDependency {

	public DirectiveDependency() {
		this(null, null, null, null);
	}

	public DirectiveDependency(String name, String equals, String min, String max) {
		super(name, equals, min, max, null, null);
	}
}
