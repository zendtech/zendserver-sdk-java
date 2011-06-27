package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

public class DeploymentPropertiesPage extends DescriptorEditorPage {

	protected IDescriptorContainer model;
	private PropertiesTreeSection appdirSection;
	private PropertiesTreeSection scriptsdirSection;

	public DeploymentPropertiesPage(IDescriptorContainer model,
			DeploymentDescriptorEditor editor, String id, String title) {
		super(editor, id, title);
		this.model = model;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction());
		mgr.update(true);

		GridLayout layout = new GridLayout(2, true);

		form.getBody().setLayout(layout);
		createTreeSections(managedForm);
	}

	private void createTreeSections(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		appdirSection = new AppTreeSection(form.getBody(), toolkit, model);
		scriptsdirSection = new ScriptsTreeSection(form.getBody(), toolkit,
				model);
	}

	@Override
	public void refresh() {
		appdirSection.refresh();
		scriptsdirSection.refresh();
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return super.isDirty();
	}

}
