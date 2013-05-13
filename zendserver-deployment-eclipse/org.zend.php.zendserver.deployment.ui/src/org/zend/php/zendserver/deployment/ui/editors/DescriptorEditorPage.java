package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.HelpAction;
import org.zend.php.zendserver.deployment.ui.actions.ToolbarAction;
import org.zend.php.zendserver.deployment.ui.contributions.ITestingSectionContribution;

/**
 * Descriptor editor form page, capable of auto-refreshing it's contents and
 * handling markers.
 */
public abstract class DescriptorEditorPage extends FormPage {

	private static final String TESTING_EXTENSION_POINT = Activator.PLUGIN_ID
			+ ".testingSectionContribution"; //$NON-NLS-1$

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
		Map<Feature, FormDecoration> toShow = editor
				.getDecorationsForFeatures(fields.keySet());
		List<Feature> toRemove = new ArrayList<Feature>(fields.keySet());
		toRemove.removeAll(toShow.keySet());
		refreshMarkers(toShow, toRemove);
	}

	private void refreshMarkers(final Map<Feature, FormDecoration> toShow,
			final List<Feature> toRemove) {
		if ((toShow == null || toShow.size() == 0)
				&& (toRemove == null || toRemove.size() == 0)) {
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
		final IToolBarManager mgr = form.getToolBarManager();
		ProjectType type = editor.getModel().getType();
		resolveContributions(mgr, type);
		mgr.update(true);
		editor.getModel().addListener(new IDescriptorChangeListener() {

			public void descriptorChanged(ChangeEvent event) {
				if (event.feature.equals(DeploymentDescriptorPackage.PKG_TYPE)) {
					if (event.newValue != null
							&& !event.newValue.equals(event.oldValue)) {
						mgr.removeAll();
						ProjectType currentType = ProjectType
								.byName((String) event.newValue);
						resolveContributions(mgr, currentType);
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								mgr.update(true);
							}
						});
					}
				}
			}
		});
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

	protected ComboField addField(ComboField field) {
		return (ComboField) fields.add(field);
	}

	protected List<ITestingSectionContribution> getTestingContributions() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(TESTING_EXTENSION_POINT);
		List<ITestingSectionContribution> result = new ArrayList<ITestingSectionContribution>();
		for (IConfigurationElement element : elements) {
			if ("contribution".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object listener = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (listener instanceof ITestingSectionContribution) {
						result.add((ITestingSectionContribution) listener);
					}
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		return result;
	}

	private void resolveContributions(final IToolBarManager mgr,
			ProjectType currentType) {
		if (currentType == ProjectType.UNKNOWN) {
			currentType = ProjectType.APPLICATION;
		}
		List<ITestingSectionContribution> contributions = getTestingContributions();
		for (ITestingSectionContribution c : contributions) {
			if (c.getType() == currentType) {
				Action action = new ToolbarAction(c.getCommand(), c.getMode(),
						c.getLabel(), c.getIcon());
				mgr.add(action);
			}
		}
		final String helpContextID = getHelpResource();
		if (helpContextID != null) {
			mgr.add(new HelpAction(helpContextID));
		}
	}
	
}
