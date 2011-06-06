package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class ParameterDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IParameter input;
	private Text idText;
	private Text displayText;
	private Text defaultText;
	private Button requiredCheck;
	private Combo typeCombo;
	private Text descrText;
	private Text longDescr;

	private boolean isRefresh;
	
	public ParameterDetailsPage(DeploymentDescriptorEditor editor) {
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
		idText.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		isRefresh = true;
		try {
			String str = input.getId();
			idText.setText(str == null ? "" : str);
			str = input.getDisplay();
			displayText.setText(str == null ? "" : str);
			str = input.getDefaultValue();
			defaultText.setText(str == null ? "" : str);
			requiredCheck.setSelection(input.isRequired());
			str = input.getType();
			typeCombo.setText(str == null ? "" : str);
			str = input.getDescription();
			descrText.setText(str == null ? "" : str);
			str = input.getLongDescription();
			longDescr.setText(str == null ? "" : str);
		} finally {
			isRefresh = false;
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if (ssel.size()==1) {
			input = (IParameter)ssel.getFirstElement();
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
		s1.setText("Parameter details");
		s1.marginWidth = 5;
		s1.marginHeight = 5;
		
		Composite client = toolkit.createComposite(s1);
		client.setLayout(new GridLayout(2, false));
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		s1.setClient(client);
		
		toolkit.createLabel(client, "Id");
		idText = toolkit.createText(client, "");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		idText.setLayoutData(gd);
		idText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				idChange(((Text)e.widget).getText());
			}
		});
		

		toolkit.createLabel(client, "Type");
		typeCombo = new Combo(client, SWT.NONE);
		typeCombo.add(IParameter.CHOICE);
		typeCombo.add(IParameter.STRING);
		typeCombo.add(IParameter.PASSWORD);
		typeCombo.add(IParameter.EMAIL);
		typeCombo.add(IParameter.CHECKBOX);
		typeCombo.add(IParameter.NUMBER);
		typeCombo.add(IParameter.HOSTNAME);
		toolkit.adapt(typeCombo, true, true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		typeCombo.setLayoutData(gd);
		typeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				typeChange(((Combo)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "");
		
		requiredCheck = toolkit.createButton(client, "This parameter is required", SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		requiredCheck.setLayoutData(gd);
		requiredCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (isRefresh) return;
				isRequiredChange(((Button)e.widget).getSelection());
			}
		});
		
		toolkit.createLabel(client, "Display text");
		displayText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		displayText.setLayoutData(gd);
		displayText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				displayChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Default value");
		defaultText = toolkit.createText(client, "");
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		defaultText.setLayoutData(gd);
		defaultText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				defaultChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Description");
		descrText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 100;
		gd.widthHint = 100;
		descrText.setLayoutData(gd);
		descrText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				descriptionChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "Long Description");
		longDescr = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 100;
		gd.widthHint = 100;
		longDescr.setLayoutData(gd);
		longDescr.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				longDescriptionChange(((Text)e.widget).getText());
			}
		});
	}

	protected void isRequiredChange(boolean selection) {
		try {
			editor.getModel().setParameterRequired(input, selection);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void idChange(String text) {
		try {
			editor.getModel().setParameterId(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void displayChange(String text) {
		try {
			editor.getModel().setParameterDisplay(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void defaultChange(String text) {
		try {
			editor.getModel().setParameterDefault(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void typeChange(String text) {
		try {
			editor.getModel().setParameterType(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void descriptionChange(String text) {
		try {
			editor.getModel().setParameterDescription(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void longDescriptionChange(String text) {
		try {
			editor.getModel().setParameterLongDescription(input, text);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
