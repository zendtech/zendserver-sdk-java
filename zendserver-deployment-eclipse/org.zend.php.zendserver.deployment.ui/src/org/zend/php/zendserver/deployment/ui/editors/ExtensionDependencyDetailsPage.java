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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;


public class ExtensionDependencyDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IModelObject input;
	
	private boolean isRefresh;
	private Text nameText;
	private Label nameLabel;
	private VersionControl version;
	
	public ExtensionDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		this.editor = editor;
		version = new VersionControl(VersionControl.EQUALS|VersionControl.CONFLICTS|VersionControl.EXCLUDE|VersionControl.RANGE);
		version.setEditor(editor);
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
		nameText.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		isRefresh = true;
		try {
			String str = input.get(IExtensionDependency.DEPENDENCY_NAME);
			nameText.setText(str == null ? "" : str);
			version.refresh();
		} finally {
			isRefresh = false;
		}
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
		s1.setText("Extension Dependency details");
		s1.setDescription("Specify extension dependency details.");
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		
		Composite client = toolkit.createComposite(s1);
		client.setLayout(new GridLayout(3, false));
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		
		nameLabel = toolkit.createLabel(client, "Extension name");
		nameText = toolkit.createText(client, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				String txt = ((Text)e.widget).getText();
				nameChange("".equals(txt) ? null : txt);
			}
		});

		version.createContents(client, toolkit);
		
		s1.setClient(client);
	}

	protected void nameChange(String text) {
		input.set(IExtensionDependency.DEPENDENCY_NAME, text);
	}
}
