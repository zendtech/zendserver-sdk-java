package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.ZendComponentsProvider;

public class ZendComponentDependencyDetailsPage extends DependencyDetailsPage {

	public ZendComponentDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, Messages.ZendComponentDependencyDetailsPage_Details,
				Messages.ZendComponentDependencyDetailsPage_SpecifyDetails);

		setNameRequired(Messages.ZendComponentDependencyDetailsPage_Name,
				new ZendComponentsProvider());
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.CONFLICTS
				| VersionControl.EXCLUDE | VersionControl.RANGE;
	}

}
