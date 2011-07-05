package org.zend.php.zendserver.deployment.ui.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.HelpAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

/**
 * 
 */
public abstract class DescriptorEditorPage extends FormPage {

	private Map<Feature, TextField> fields = new HashMap<Feature, TextField>();

	protected DeploymentDescriptorEditor editor;

	public DescriptorEditorPage(DeploymentDescriptorEditor editor, String id,
			String title) {
		super(editor, id, title);
		this.editor = editor;
	}

	@Override
	public void setActive(boolean active) {
		refresh();
	}

	public abstract void refresh();

	public void showStatuses(List<ValidationStatus> statuses) {
		Set<Feature> keys = fields.keySet();

		Map<Feature, ValidationStatus> toShow = new HashMap<Feature, ValidationStatus>();

		for (ValidationStatus s : statuses) {
			if (keys.contains(s.getProperty())) {
				toShow.put(s.getProperty(), s);
			}
		}

		for (TextField f : fields.values()) {
			Feature key = f.getKey();
			ValidationStatus status = toShow.get(key);
			if (status == null) {
				f.setErrorMessage(null);
			} else if (status.getSeverity() == ValidationStatus.ERROR) {
				f.setErrorMessage(status.getMessage());
			} else if (status.getSeverity() == ValidationStatus.WARNING) {
				f.setWarningMessage(status.getMessage());
			}
		}
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading(form.getForm());
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction());
		
		final String helpContextID = getHelpResource();
		if (helpContextID != null) {
			mgr.add(new HelpAction(helpContextID));
		}

		mgr.update(true);
	}

	/**
	 * Override this method to provide help context
	 * 
	 * @return
	 */
	protected String getHelpResource() {
		return "to be assigned";
	}

	protected TextField addField(TextField field) {
		fields.put(field.getKey(), field);
		return field;
	}
}
