package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;

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
	public void refresh() {
		appdirSection.refresh();
		scriptsdirSection.refresh();
	}

	@Override
	public void setActive(boolean active) {
		if (active) {
			DeploymentDescriptorEditor editor = ((DeploymentDescriptorEditor) getEditor());
			IDocument doc = editor.getDocumentProvider().getDocument(
					editor.getPropertiesInput());
			model.initializeMappingModel(doc);
		}
		super.setActive(active);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		GridLayout layout = new GridLayout(2, true);
		ScrolledForm form = managedForm.getForm();
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
}
