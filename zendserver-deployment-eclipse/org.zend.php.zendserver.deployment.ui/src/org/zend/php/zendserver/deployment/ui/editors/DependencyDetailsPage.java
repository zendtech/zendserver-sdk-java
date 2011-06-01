package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Dependency;


public class DependencyDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IDependency input;
	private Combo typeCombo;

	private boolean isRefresh;
	private Text nameText;
	private Text minText;
	private Text maxText;
	private Text equalsText;
	private Text conflictsText;
	
	public DependencyDetailsPage(DeploymentDescriptorEditor editor) {
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
		typeCombo.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		isRefresh = true;
		try {
			String str = ((Dependency)input).getName();
			nameText.setText(str == null ? "" : str);
			str = ((Dependency)input).getMin();
			minText.setText(str == null ? "" : str);
			str = ((Dependency)input).getMax();
			maxText.setText(str == null ? "" : str);
			str = ((Dependency)input).getEquals();
			equalsText.setText(str == null ? "" : str);
			str = ((Dependency)input).getConflicts();
			conflictsText.setText(str == null ? "" : str);
		} finally {
			isRefresh = false;
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IDependency)ssel.getFirstElement();
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
		s1.setText("Dependency details");
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		
		Composite client = toolkit.createComposite(s1);
		client.setLayout(new GridLayout(2, false));
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		toolkit.createLabel(client, "Type");
		typeCombo = new Combo(client, SWT.NONE);
		typeCombo.add("php");
		typeCombo.add("extension");
		typeCombo.add("directive");
		toolkit.adapt(typeCombo, true, true);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		typeCombo.setLayoutData(gd);
		typeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				typeChange(((Combo)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Name");
		nameText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				nameChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Min");
		minText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		minText.setLayoutData(gd);
		minText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				minChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Max");
		maxText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		maxText.setLayoutData(gd);
		maxText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				maxChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Equals");
		equalsText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		equalsText.setLayoutData(gd);
		equalsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				equalsChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Conflicts");
		conflictsText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		conflictsText.setLayoutData(gd);
		conflictsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				conflictsChange(((Text)e.widget).getText());
			}
		});
		
		s1.setClient(client);
	}

	protected void typeChange(String text) {
		// TODO Auto-generated method stub
		
	}

	protected void conflictsChange(String text) {
		try {
			editor.getModel().setDependencyConflicts(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void equalsChange(String text) {
		try {
			editor.getModel().setDependencyEquals(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void maxChange(String text) {
		try {
			editor.getModel().setDependencyMax(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void minChange(String text) {
		try {
			editor.getModel().setDependencyMin(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void nameChange(String text) {
		try {
			editor.getModel().setDependencyName(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
