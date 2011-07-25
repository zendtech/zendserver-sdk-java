package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.core.IncrementalDeploymentBuilder;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.HelpAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

/**
 * Descriptor editor form page, capable of auto-refreshing it's contents and handling markers.
 */
public abstract class DescriptorEditorPage extends FormPage {

	private Map<Feature, TextField> fields = new HashMap<Feature, TextField>();

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
		refreshMarkers(toShow, null);
	}
	
	public void updateMarkers(IMarkerDelta[] markerDeltas) {
		Set<Feature> keyset = fields.keySet();
		Map<Integer, Feature> featureIds = new HashMap<Integer, Feature>();
		for (Feature f : keyset) {
			featureIds.put(f.id, f);
		}

		Map<Feature, FormDecoration> toShow = new HashMap<Feature, FormDecoration>();
		List<Feature> toRemove = new ArrayList<Feature>();

		for (IMarkerDelta delta : markerDeltas) {
			int kind = delta.getKind();
			int featureId = delta.getAttribute(IncrementalDeploymentBuilder.FEATURE_ID, -1);
			if (featureId != -1) { 
				Feature f = featureIds.get(featureId);
				if (f != null) {
					if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
						toShow.put(f, markerDeltaToDecoration(delta));
					} else if (kind == IResourceDelta.REMOVED) {
						toRemove.add(f);
					}
				}
			}
		}
		
		toRemove.removeAll(toShow.keySet());
		
		refreshMarkers(toShow, toRemove);
	}
	
	private FormDecoration markerDeltaToDecoration(IMarkerDelta delta) {
		String message = delta.getAttribute(IMarker.MESSAGE, null);
		int severity = delta.getAttribute(IMarker.SEVERITY, 0);
		return new FormDecoration(message, severity);
	}

	private void refreshMarkers(final Map<Feature, FormDecoration> toShow,
			final List<Feature> toRemove) {
		if ((toShow == null || toShow.size() == 0) && (toRemove == null || toRemove.size() == 0)) {
			return;
		}
		
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (toRemove != null) {
					for (Feature feature : toRemove) {
						TextField field = fields.get(feature);
						if (field != null) {
							field.setDecoration(null);
						}
					}
				}
				
				if (toShow != null) {
					for (Map.Entry<Feature, FormDecoration> entry : toShow.entrySet()) {
						FormDecoration status = entry.getValue();
						TextField field = fields.get(entry.getKey());
						if (field != null) {
							field.setDecoration(status);
						}
					}
				}
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
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction(editor.getProject()));
		
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
		fields.put(field.getKey(), field);
		return field;
	}
}
