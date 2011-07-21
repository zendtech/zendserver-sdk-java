package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
			nameText.setText(str == null ? "" : str); //$NON-NLS-1$
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
		
		toolkit.createLabel(ext,
				Messages.ExtensionDependencyDetailsPage_ExtensionName);
		nameText = new Combo(ext, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh)
					return;
				String txt = ((Combo) e.widget).getText();
				nameChange("".equals(txt) ? null : txt); //$NON-NLS-1$
			}
		});
	}
	
	protected void createContentAssist() {
		PHPExtensionsProvider provider = new PHPExtensionsProvider();
		provider.init();
		nameText.setItems(provider.getNames());
	}

	protected void nameChange(String text) {
		if (input != null) {
			input.set(DeploymentDescriptorPackage.DEPENDENCY_NAME, text);
		}
	}
}
