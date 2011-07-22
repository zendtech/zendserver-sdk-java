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

public class ExtensionDependencyDetailsPage extends SectionDetailPage {

	private boolean isRefresh;
	private Combo nameText;

	public ExtensionDependencyDetailsPage() {
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

	@Override
	protected Section addSection(Composite parent, FormToolkit toolkit) {
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		s1.setText(Messages.ExtensionDependencyDetailsPage_extensionDependencyDetails);
		s1.setDescription(Messages.ExtensionDependencyDetailsPage_SpecifyExtensionDependencyDetails);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB));
		return s1;
	}
	
	@Override
	protected void addComponent(FormToolkit toolkit, Composite general) {
		general.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		general.setLayoutData(gd);

		Composite ext = toolkit.createComposite(general);
		ext.setLayout(new GridLayout(3, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		ext.setLayoutData(gd);
		
		PHPExtensionsProvider provider = new PHPExtensionsProvider();
		provider.init();
		final Composite hint = toolkit.createComposite(ext);		
		hint.setLayout(new GridLayout(3, false));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
		hint.setLayoutData(data);
		final TextAssistField field = new TextAssistField(null, null, Messages.ExtensionDependencyDetailsPage_ExtensionName, provider.getNames());
		field.create(hint, toolkit);		
	}
	
	protected void nameChange(String text) {
		if (input != null) {
			input.set(DeploymentDescriptorPackage.DEPENDENCY_NAME, text);
		}
	}
}
