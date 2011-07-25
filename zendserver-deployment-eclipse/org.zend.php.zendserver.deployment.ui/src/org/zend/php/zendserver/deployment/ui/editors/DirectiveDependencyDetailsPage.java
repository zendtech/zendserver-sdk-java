package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.PHPDirectivesProvider;

public class DirectiveDependencyDetailsPage extends DependencyDetailsPage {

	public DirectiveDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, 
				Messages.DirectiveDependencyDetailsPage_DirectiveDependencyDetails,
				Messages.DirectiveDependencyDetailsPage_SpecifyDirectiveProperties);
		setNameRequired(Messages.DirectiveDependencyDetailsPage_Directive,
				new PHPDirectivesProvider());
	}

	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.RANGE;
	}

	@Override
	protected Section addSection(Composite parent, FormToolkit toolkit) {
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		s1.setText(Messages.DirectiveDependencyDetailsPage_DirectiveDependencyDetails);
		s1.setDescription(Messages.DirectiveDependencyDetailsPage_SpecifyDirectiveProperties);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB));
		return s1;
	}
}
