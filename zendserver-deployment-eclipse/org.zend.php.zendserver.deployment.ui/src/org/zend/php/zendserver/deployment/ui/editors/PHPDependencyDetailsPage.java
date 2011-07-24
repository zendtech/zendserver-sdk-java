package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;

public class PHPDependencyDetailsPage extends DependencyDetailsPage {

	public PHPDependencyDetailsPage() {
		super(Messages.PHPDependencyDetailsPage_PHPDepDetails,
				Messages.PHPDependencyDetailsPage_Version);
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE
				| VersionControl.EXCLUDE;
	}
}
