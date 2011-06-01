package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;

public class ExtensionDependency extends Dependency implements IExtensionDependency {

	public ExtensionDependency() {
		this(null, null, null, null, null, null);
	}
	
	public ExtensionDependency(String name, String equals, String min, String max, String[] excludes, String conflicts) {
		super(name, equals, min, max, excludes, conflicts);
	}
}
