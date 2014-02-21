package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.editors.DescriptorEditorPage.FormDecoration;

public abstract class DescriptorDetailsPage implements IDetailsPage {

	protected FieldsContainer fields = new FieldsContainer();
	
	protected IModelObject input;
	
	protected DeploymentDescriptorEditor editor;
	
	public DescriptorDetailsPage(DeploymentDescriptorEditor editor) {
		this.editor = editor;
	}
	
	public void showMarkers() {
		if (input == null) {
			return;
		}
		
		IDeploymentDescriptor imc = editor.getModel();
		int index = -1;
		if (imc != null) {
			Feature f = DeploymentDescriptorFactory.getFeature(input);
			index = imc.getChildren(f).indexOf(input);
		}
		
		List<Feature> properties = Arrays.asList(input.getPropertyNames());
		Map<Feature, FormDecoration> decorations = null;
		if (index != -1) {
			decorations = editor.getDecorationsForFeatures(properties, index);
		} else {
			decorations = editor.getDecorationsForFeatures(properties);
		}
		
		List<Feature> toRemove = new ArrayList<Feature>(properties);
		toRemove.removeAll(decorations.keySet());
		fields.refreshMarkers(decorations, toRemove);
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IModelObject)ssel.getFirstElement();
		}
		else
			input = null;
		fields.setInput(input);
		refresh();
	}
	
	public void refresh() {
		fields.refresh();
		showMarkers();
	}

	public void setFocus() {
		fields.fields().iterator().next().setFocus();
	}
}
