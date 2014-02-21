package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;

public class ZendServerDependencyDetailsPage extends DependencyDetailsPage {

	public ZendServerDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, Messages.ZendServerDependencyDetailsPage_Details,
				Messages.ZendServerDependencyDetailsPage_Version);
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE
				| VersionControl.EXCLUDE;
	}
}
