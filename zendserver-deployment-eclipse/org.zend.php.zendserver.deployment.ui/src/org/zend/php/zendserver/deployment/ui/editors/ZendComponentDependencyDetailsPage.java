package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.PHPExtensionsProvider;
import org.zend.php.zendserver.deployment.ui.contentassist.ZendComponentsProvider;

public class ZendComponentDependencyDetailsPage extends SectionDetailPage {

	private boolean isRefresh;
	private Combo nameText;

	public ZendComponentDependencyDetailsPage() {
		version = new VersionControl(VersionControl.EQUALS
				| VersionControl.CONFLICTS | VersionControl.EXCLUDE
				| VersionControl.RANGE, input);
		addComponent = true;
	}

	public void refresh() {
		isRefresh = true;
		try {
			String str = input.get(DeploymentDescriptorPackage.DEPENDENCY_NAME);
			// nameText.setText(str == null ? "" : str); //$NON-NLS-1$
			version.refresh();
		} finally {
			isRefresh = false;
		}
	}

	protected void createContentAssist() {
		ZendComponentsProvider provider = new ZendComponentsProvider();
		provider.init();
		nameText.setItems(provider.getNames());
	}

	protected void nameChange(String text) {
		if (input != null) {
			input.set(DeploymentDescriptorPackage.DEPENDENCY_NAME, text);
		}
	}

	@Override
	protected Section addSection(Composite parent, FormToolkit toolkit) {
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		s1.setText(Messages.ZendComponentDependencyDetailsPage_Details);
		s1.setDescription(Messages.ZendComponentDependencyDetailsPage_SpecifyDetails);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB));
		return s1;
	}

	protected void addComponent(FormToolkit toolkit, Composite general) {
		general.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		general.setLayoutData(gd);

		Composite directive = toolkit.createComposite(general);
		directive.setLayout(new GridLayout(3, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		directive.setLayoutData(gd);

		// TODO : zend conponent proposals
		PHPExtensionsProvider provider = new PHPExtensionsProvider();
		provider.init();
		final Composite hint = toolkit.createComposite(directive);		
		hint.setLayout(new GridLayout(3, false));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
		hint.setLayoutData(data);
		final TextAssistField field = new TextAssistField(null, null, Messages.ZendComponentDependencyDetailsPage_Name, provider.getNames());
		field.create(hint, toolkit);
	}
}
