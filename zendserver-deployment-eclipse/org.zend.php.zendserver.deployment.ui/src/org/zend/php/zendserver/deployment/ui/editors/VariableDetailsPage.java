package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;


public class VariableDetailsPage implements IDetailsPage {

	private IManagedForm mform;
	
	private TextField name;
	private TextField value;	
	
	public VariableDetailsPage(DeploymentDescriptorEditor editor) {
		name = new TextField(null, IVariable.NAME, "Name");
		value = new TextField(null, IVariable.VALUE, "Value");
	}
	
	public void initialize(IManagedForm form) {
		this.mform = form;
	}

	public void dispose() {
		// TODO Auto-generated method stub

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
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		IModelObject input;
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
		s1.setText("Variable details");
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
