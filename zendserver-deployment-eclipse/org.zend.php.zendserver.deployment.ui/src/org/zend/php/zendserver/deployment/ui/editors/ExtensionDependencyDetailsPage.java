package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.PHPExtensionsProvider;

public class ExtensionDependencyDetailsPage extends DependencyDetailsPage {

	public ExtensionDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, 
				Messages.ExtensionDependencyDetailsPage_extensionDependencyDetails,
				Messages.ExtensionDependencyDetailsPage_SpecifyExtensionDependencyDetails);
		
		setNameRequired(Messages.ExtensionDependencyDetailsPage_ExtensionName,
				new PHPExtensionsProvider());
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.CONFLICTS
				| VersionControl.EXCLUDE | VersionControl.RANGE;
	}
}
