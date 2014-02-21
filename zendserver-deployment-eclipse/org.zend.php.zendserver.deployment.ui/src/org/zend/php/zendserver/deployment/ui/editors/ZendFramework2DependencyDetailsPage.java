package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;

public class ZendFramework2DependencyDetailsPage extends DependencyDetailsPage {

	public ZendFramework2DependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, Messages.ZendFrameworkDependencyDetailsPage_ZF2DepDetails,
				Messages.ZendFrameworkDependencyDetailsPage_ZF2DepVersion);
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE
				| VersionControl.EXCLUDE;
	}
}
