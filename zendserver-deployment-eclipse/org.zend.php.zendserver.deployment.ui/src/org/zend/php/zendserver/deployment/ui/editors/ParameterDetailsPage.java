package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class ParameterDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IParameter input;
	private Text idText;
	private Text displayText;
	private Text defaultText;
	private Combo defaultCombo;
	private Text validationText;
	private Button requiredCheck;
	private Button readonlyCheck;
	private Combo typeCombo;
	private Combo identical;
	private Text descrText;

	private boolean isRefresh;

	private Label defaultTextLabel;
	private Label defaultComboLabel;
	private Label validationTextLabel;

	private Section section;
	
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
			defaultCombo.setText(str == null ? "" : str);
			requiredCheck.setSelection(input.isRequired());
			readonlyCheck.setSelection(input.isReadOnly());
			List<String> validValues = input.getValidValues();
			if (validValues != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < validValues.size(); i++) {
					sb.append(validValues.get(i)).append("\n");
				}
				validationText.setText(sb.toString());
			} else {
				validationText.setText("");
			}
			str = input.getType();
			typeCombo.setText(str == null ? "" : str);
			str = input.getIdentical();
			identical.setText(str == null ? "" : str);
			str = input.getDescription();
			descrText.setText(str == null ? "" : str);
			refreshParametersList();
			showChoiceWidgets(input.getType());
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
		section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText("Parameter details");
		section.marginWidth = 5;
		section.marginHeight = 5;
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		section.setClient(client);
		
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
		
		defaultTextLabel = toolkit.createLabel(client, "Default value");
		defaultTextLabel.setLayoutData(new GridData());
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
		
		defaultComboLabel = toolkit.createLabel(client, "Default value");
		defaultComboLabel.setLayoutData(new GridData());
		defaultCombo = new Combo(client, SWT.NONE);
		toolkit.adapt(defaultCombo, true, true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		defaultCombo.setLayoutData(gd);
		defaultCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				defaultChange(((Combo)e.widget).getText());
			}
		});
		
		validationTextLabel = toolkit.createLabel(client, "Valid values");
		validationTextLabel.setLayoutData(new GridData());
		validationText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 100;
		gd.widthHint = 100;
		validationText.setLayoutData(gd);
		validationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				validationChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "");
		
		readonlyCheck = toolkit.createButton(client, "This parameter is read only", SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		readonlyCheck.setLayoutData(gd);
		readonlyCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (isRefresh) return;
				isReadonlyChange(((Button)e.widget).getSelection());
			}
		});
		
		toolkit.createLabel(client, "Identical");
		identical= new Combo(client, SWT.NONE);
		toolkit.adapt(identical, true, true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 100;
		identical.setLayoutData(gd);
		identical.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				identicalChange(((Combo)e.widget).getText());
			}
		});
		editor.getDescriptorContainer().addChangeListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(Object target) {
				if (target instanceof IParameter) {
					identical.getDisplay().asyncExec(new Runnable() {
						public void run() {
							refreshParametersList();
						};
					});
				}
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
	}
	
	private void refreshParametersList() {
		List<IParameter> params = editor.getDescriptorContainer().getDescriptorModel().getParameters();
		for (int i = 0; i < params.size(); i++) {
			IParameter param = params.get(i);
			String paramId = param.getId();
			
			if (param.equals(input)) {
				continue;
			}
			
			if (identical.getItemCount() > i) {
				if (! paramId.equals(identical.getItem(i))) {
					identical.setItem(i,  paramId);
				}
			} else {
				identical.add(paramId);
			}
		}
		
	}

	protected void isRequiredChange(boolean selection) {
		input.setRequired(selection);
	}
	
	protected void isReadonlyChange(boolean selection) {
		input.setReadOnly(selection);
	}

	protected void idChange(String text) {
		input.setId(text);
	}

	protected void displayChange(String text) {
		input.setDisplay(text);
	}

	protected void defaultChange(String text) {
		input.setDefaultValue(text);
	}

	protected void typeChange(String text) {
		input.setType(text);
		
		showChoiceWidgets(text);
	}
	
	private void showChoiceWidgets(String text) {
		boolean showChoiceWidgets = IParameter.CHOICE.equals(text);
		defaultText.setVisible(!showChoiceWidgets);
		defaultTextLabel.setVisible(!showChoiceWidgets);
		((GridData)defaultText.getLayoutData()).exclude = showChoiceWidgets;
		((GridData)defaultTextLabel.getLayoutData()).exclude = showChoiceWidgets;
		
		defaultCombo.setVisible(showChoiceWidgets);
		defaultComboLabel.setVisible(showChoiceWidgets);
		((GridData)defaultCombo.getLayoutData()).exclude = !showChoiceWidgets;
		((GridData)defaultComboLabel.getLayoutData()).exclude = !showChoiceWidgets;
		
		validationText.setVisible(showChoiceWidgets);
		validationTextLabel.setVisible(showChoiceWidgets);
		((GridData)validationText.getLayoutData()).exclude = !showChoiceWidgets;
		((GridData)validationTextLabel.getLayoutData()).exclude = !showChoiceWidgets;
		
		section.layout(true);
		Point size = section.getSize();
		Point newsize = section.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		section.setSize(size.x, newsize.y);
	}

	protected void identicalChange(String text) {
		input.setIdentical(text);
	}
	
	protected void validationChange(String text) {
		String[] newParams = text.split("\n");
		
		input.getValidValues().clear();
		input.getValidValues().addAll(Arrays.asList(newParams));
		
		String currentDefault = defaultCombo.getText();
		defaultCombo.setItems(newParams);
		int newIndex = Arrays.asList(newParams).indexOf(currentDefault);
		if (newIndex != -1) {
			defaultCombo.select(newIndex);
		} else {
			defaultCombo.setText(currentDefault);
		}
	}
	
	protected void descriptionChange(String text) {
		input.setDescription(text);
	}
}
