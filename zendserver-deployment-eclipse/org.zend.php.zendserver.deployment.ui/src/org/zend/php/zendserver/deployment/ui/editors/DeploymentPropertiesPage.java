package org.zend.php.zendserver.deployment.ui.editors;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
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
		appdirSection = new AppTreeSection(getEditor(), form.getBody(),
				toolkit, model);
		scriptsdirSection = new ScriptsTreeSection(getEditor(), form.getBody(),
				toolkit, model);
	}

	@Override
	public void refresh() {
		appdirSection.refresh();
		scriptsdirSection.refresh();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		super.doSave(monitor);
		try {
			model.getMappingModel().store();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty() {
		if (appdirSection != null && scriptsdirSection != null) {
			return appdirSection.isDirty() || scriptsdirSection.isDirty();
		}
		return false;
	}

}
