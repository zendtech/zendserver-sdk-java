package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.zend.php.zendserver.deployment.ui.Messages;

public class PHPDependencyDetailsPage extends SectionDetailPage {

	public PHPDependencyDetailsPage() {
		version = new VersionControl(VersionControl.EQUALS
				| VersionControl.RANGE | VersionControl.EXCLUDE, input);
	}

	@Override
	protected Section addSection(Composite parent, FormToolkit toolkit) {
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		s1.setText(Messages.PHPDependencyDetailsPage_PHPDepDetails);
		s1.setDescription(Messages.PHPDependencyDetailsPage_Version);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB));
		return s1;
	}
}
