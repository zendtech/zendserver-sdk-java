package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;


public class VariableDetailsPage extends DescriptorDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	
	private List<EditorField> fields = new ArrayList<EditorField>();
	private EditorField name;
	private EditorField value;

	private IModelObject input;
	
	public VariableDetailsPage(DeploymentDescriptorEditor editor) {
		this.editor = editor;
		name = addField(new TextField(null, DeploymentDescriptorPackage.VAR_NAME, Messages.VariableDetailsPage_Name));
		value = addField(new TextField(null, DeploymentDescriptorPackage.VALUE, Messages.VariableDetailsPage_Value));
	}
	
	private EditorField addField(EditorField field) {
		fields.add(field);
		return field;
	}

	public void initialize(IManagedForm form) {
		this.mform = form;
	}

	public void dispose() {
		// empty
	}

	public boolean isDirty() {
		return false;
	}

	public void commit(boolean onSave) {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void setFocus() {
		name.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		name.refresh();
		value.refresh();
		refreshDecorations();
	}

	private void refreshDecorations() {
		IDeploymentDescriptor imc = editor.getModel();
		int index = -1;
		if (imc != null) {
			Feature f = DeploymentDescriptorFactory.getFeature(input);
			index = imc.getChildren(f).indexOf(input);
		}
		
		Map<Feature, FormDecoration> decorations = null;
		if (index != -1) {
			decorations = editor.getDecorationsForFeatures(Arrays.asList(input.getPropertyNames()), index);
		} else {
			decorations = editor.getDecorationsForFeatures(Arrays.asList(input.getPropertyNames()));
		}
		
		for (Entry<Feature, FormDecoration> e : decorations.entrySet()) {
			Feature feature = e.getKey();
			for (EditorField field : fields) {
				if (field.getKey() == feature) {
					field.setDecoration(e.getValue());
				}
			}
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IModelObject)ssel.getFirstElement();
		}
		else
			input = null;
		name.setInput(input);
		value.setInput(input);
		refresh();
	}

	public void createContents(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.bottomMargin = 0;
		layout.numColumns = 1;
		parent.setLayout(layout);
		
		FormToolkit toolkit = mform.getToolkit();
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		s1.setText(Messages.VariableDetailsPage_Details);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		
		Composite client = toolkit.createComposite(s1);
		client.setLayout(new GridLayout(3, false));
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		s1.setClient(client);
		
		name.create(client, toolkit);
		value.create(client, toolkit);
	}
}
