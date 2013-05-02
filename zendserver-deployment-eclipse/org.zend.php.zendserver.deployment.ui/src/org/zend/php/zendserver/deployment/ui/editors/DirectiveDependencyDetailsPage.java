package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.PHPDirectivesProvider;

public class DirectiveDependencyDetailsPage extends DependencyDetailsPage {

	public DirectiveDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, 
				Messages.DirectiveDependencyDetailsPage_DirectiveDependencyDetails,
				Messages.DirectiveDependencyDetailsPage_SpecifyDirectiveProperties);
		setNameRequired(Messages.DirectiveDependencyDetailsPage_Directive,
				new PHPDirectivesProvider());
		setEqualsLabel(Messages.DirectiveDependencyDetailsPage_0);
	}

	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE;
	}
}
