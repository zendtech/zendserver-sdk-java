package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;

public class ZendServerDependencyDetailsPage extends DependencyDetailsPage {

	public ZendServerDependencyDetailsPage() {
		super(Messages.ZendServerDependencyDetailsPage_Details,
				Messages.ZendServerDependencyDetailsPage_Version);
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE
				| VersionControl.EXCLUDE;
	}
}
