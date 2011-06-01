package org.zend.php.zendserver.deployment.core.internal.descriptor;

import org.eclipse.core.runtime.IPath;
import org.zend.php.zendserver.deployment.core.descriptor.IMapping;


public class Mapping implements IMapping {

	private IPath path;
	private boolean isContent;

	public Mapping(IPath path, boolean isContent) {
		super();
		this.path = path;
		this.isContent = isContent;
	}

	public IPath getPath() {
		return path;
	}

	public boolean isContent() {
		return isContent;
	}

}
