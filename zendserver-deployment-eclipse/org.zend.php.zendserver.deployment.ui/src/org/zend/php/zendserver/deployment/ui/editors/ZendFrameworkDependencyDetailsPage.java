package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;

public class ZendFrameworkDependencyDetailsPage extends DependencyDetailsPage {

	public ZendFrameworkDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, Messages.ZendFrameworkDependencyDetailsPage_ZFDepDetails,
				Messages.ZendFrameworkDependencyDetailsPage_ZFDepVersion);
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE
				| VersionControl.EXCLUDE;
	}
}
