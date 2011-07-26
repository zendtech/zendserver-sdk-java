package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.menus.IMenuService;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.actions.HelpAction;

/**
 * Descriptor editor form page, capable of auto-refreshing it's contents and handling markers.
 */
public abstract class DescriptorEditorPage extends FormPage {

	private static final String HEAD = "head"; //$NON-NLS-1$

	private FieldsContainer fields = new FieldsContainer();

	protected DeploymentDescriptorEditor editor;

	public static class FormDecoration {
		public String message;
		public int severity;
		public FormDecoration(String message, int severity) {
			this.message = message;
			this.severity = severity;
		}
	}
	
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
	
	public void showMarkers() {
		Map<Feature, FormDecoration>toShow = editor.getDecorationsForFeatures(fields.keySet());
		List<Feature> toRemove = new ArrayList<Feature>(fields.keySet());
		toRemove.removeAll(toShow.keySet());
		refreshMarkers(toShow, toRemove);
	}
	
	private void refreshMarkers(final Map<Feature, FormDecoration> toShow,
			final List<Feature> toRemove) {
		if ((toShow == null || toShow.size() == 0) && (toRemove == null || toRemove.size() == 0)) {
			return;
		}
		
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				fields.refreshMarkers(toShow, toRemove);
			}
		});
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading(form.getForm());
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new GroupMarker(HEAD));
		IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager((ContributionManager) mgr, DeploymentDescriptorEditor.TOOLBAR_LOCATION_URI);
		
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
		return null; // TODO to be assigned
	}

	protected TextField addField(TextField field) {
		return (TextField) fields.add(field);
	}
}
