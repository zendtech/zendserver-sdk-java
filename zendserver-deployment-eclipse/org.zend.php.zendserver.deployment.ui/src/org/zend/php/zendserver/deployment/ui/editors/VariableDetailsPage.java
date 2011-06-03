package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;


public class VariableDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IVariable input;
	
	private boolean isRefresh;

	private Text variableName;
	private Text variableValue;	
	
	public VariableDetailsPage(DeploymentDescriptorEditor editor) {
		this.editor = editor;
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
		// TODO Auto-generated method stub

	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		isRefresh = true;
		try {
			String name = input.getName();
			variableName.setText(name == null ? "" : name);
			String value = input.getValue();
			variableValue.setText(value == null ? "" : value);
		} finally {
			isRefresh = false;
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IVariable)ssel.getFirstElement();
		}
		else
			input = null;
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
		client.setLayout(new GridLayout(2, false));
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		s1.setClient(client);
		
		toolkit.createLabel(client, "Name");
		variableName = toolkit.createText(client, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		variableName.setLayoutData(gd);
		variableName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				variableNameChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Value");
		variableValue = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		variableValue.setLayoutData(gd);
		variableValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				variableValueChange(((Text)e.widget).getText());
			}
		});
	}

	protected void variableValueChange(String text) {
		try {
			editor.getModel().setVariableValue(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void variableNameChange(String text) {
		try {
			editor.getModel().setVariableName(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
