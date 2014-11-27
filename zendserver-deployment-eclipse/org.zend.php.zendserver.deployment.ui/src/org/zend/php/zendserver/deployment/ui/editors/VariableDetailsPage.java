package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.ui.Messages;


public class VariableDetailsPage extends DescriptorDetailsPage {
	
	private IManagedForm mform;
	
	public VariableDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor);
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

	public boolean isStale() {
		return false;
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
		
		IMessageManager mmng = mform.getMessageManager();
		
		EditorField name = fields.add(new TextField(null, DeploymentDescriptorPackage.VAR_NAME, Messages.VariableDetailsPage_Name, mmng));
		name.create(client, toolkit);
		EditorField value = fields.add(new TextField(null, DeploymentDescriptorPackage.VALUE, Messages.VariableDetailsPage_Value, mmng));
		value.create(client, toolkit);
		
		toolkit.paintBordersFor(client);
	}
}
