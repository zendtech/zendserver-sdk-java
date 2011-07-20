package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.Messages;


public class PHPDependencyDetailsPage implements IDetailsPage {
	
	private IManagedForm mform;
	private IModelObject input;
	
	private VersionControl version;
	
	public PHPDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		version = new VersionControl(VersionControl.EQUALS|VersionControl.RANGE|VersionControl.EXCLUDE);
		version.setEditor(editor);
	}
	
	public void initialize(IManagedForm form) {
		this.mform = form;
	}

	public void dispose() {
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
		version.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		version.refresh();
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IModelObject) ssel.getFirstElement();
		}
		else
			input = null;
		version.setInput(input);
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
		s1.setText(Messages.PHPDependencyDetailsPage_PHPDepDetails);
		s1.setDescription(Messages.PHPDependencyDetailsPage_Version);
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		
		Composite client = toolkit.createComposite(s1);
		version.createContents(client, toolkit);
		
		s1.setClient(client);
	}
}
