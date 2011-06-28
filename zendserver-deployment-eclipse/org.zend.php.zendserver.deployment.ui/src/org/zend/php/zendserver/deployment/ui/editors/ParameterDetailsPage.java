package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
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
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public class ParameterDetailsPage implements IDetailsPage {

	private DeploymentDescriptorEditor editor;
	
	private IManagedForm mform;
	private IParameter input;
	
	private TextField id;
	private TextField display;
	private TextField defaultValue;
	private ComboField defaultCombo;
	private Text validationText;
	private CheckboxField required;
	private CheckboxField readonly;
	private ComboField type;
	private ComboField identical;
	private TextField description;

	private boolean isRefresh;

	private Label validationTextLabel;

	private Section section;
	
	public ParameterDetailsPage(DeploymentDescriptorEditor editor) {
		this.editor = editor;
		
		id = new TextField(null, DeploymentDescriptorPackage.ID, "Id");
		display = new TextField(null, DeploymentDescriptorPackage.DISPLAY, "Display text");
		defaultValue = new TextField(null, DeploymentDescriptorPackage.DEFAULTVALUE, "Default value");
		defaultCombo = new ComboField(null, DeploymentDescriptorPackage.DEFAULTVALUE, "Default value");
		description = new TextField(null, DeploymentDescriptorPackage.PARAM_DESCRIPTION, "Description");
		type = new ComboField(null, DeploymentDescriptorPackage.TYPE, "Type");
		type.setItems(new String[] {
				IParameter.CHOICE,
				IParameter.STRING,
				IParameter.PASSWORD,
				IParameter.EMAIL,
				IParameter.CHECKBOX,
				IParameter.NUMBER,
				IParameter.HOSTNAME
		});
		identical = new ComboField(null, DeploymentDescriptorPackage.IDENTICAL, "Identical");
		required = new CheckboxField(null, DeploymentDescriptorPackage.REQUIRED, "This parameter is required");
		readonly = new CheckboxField(null, DeploymentDescriptorPackage.READONLY, "This parameter is read only");
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
		id.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		isRefresh = true;
		try {
			id.refresh();
			display.refresh();
			defaultValue.refresh();
			defaultCombo.refresh();
			required.refresh();
			readonly.refresh();
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
			type.refresh();
			identical.refresh();
			description.refresh();
			refreshParametersList();
			showChoiceWidgets();
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
		
		id.setInput(input);
		defaultValue.setInput(input);
		display.setInput(input);
		description.setInput(input);
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
		client.setLayout(new GridLayout(3, false));
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		section.setClient(client);
		
		id.create(client, toolkit);
		((GridData)id.getText().getLayoutData()).widthHint = 100;
		
		type.create(client, toolkit);
		((GridData)type.getCombo().getLayoutData()).widthHint = 100;
		
		toolkit.createLabel(client, "");
		
		required.create(client, toolkit);
		
		display.create(client, toolkit);
		((GridData)display.getText().getLayoutData()).widthHint = 100;
		
		defaultValue.create(client, toolkit);
		((GridData)defaultValue.getText().getLayoutData()).widthHint = 100;
		
		defaultCombo.create(client, toolkit);
		
		validationTextLabel = toolkit.createLabel(client, "Valid values");
		validationTextLabel.setLayoutData(new GridData());
		validationText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 100;
		gd.widthHint = 100;
		gd.horizontalSpan = 2;
		validationText.setLayoutData(gd);
		validationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isRefresh) return;
				validationChange(((Text)e.widget).getText());
			}
		});
		
		toolkit.createLabel(client, "");
		
		readonly.create(client, toolkit);
		
		identical.create(client, toolkit);
		editor.getModel().addListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(ChangeEvent event) {
				if (event.target instanceof IParameter) {
					section.getDisplay().asyncExec(new Runnable() {
						public void run() {
							refreshParametersList();
							showChoiceWidgets();
						};
					});
				}
			}
		});
		
		description.create(client, toolkit);
		gd = ((GridData)description.getText().getLayoutData());
		gd.heightHint = 100;
		gd.widthHint = 100;
	}
	
	private void refreshParametersList() {
		List<IParameter> params = editor.getDescriptorContainer().getDescriptorModel().getParameters();
		
		String[] items = new String[params.size()];
		for (int i = 0; i < params.size(); i++) {
			IParameter param = params.get(i);
			String paramId = param.getId();
			items[i] = paramId;
		}
		
		identical.setItems(items);
	}
	
	private void showChoiceWidgets() {
		boolean showChoiceWidgets = IParameter.CHOICE.equals(input.getType());
		
		defaultValue.setVisible(!showChoiceWidgets);
		defaultCombo.setVisible(showChoiceWidgets);
		
		validationText.setVisible(showChoiceWidgets);
		validationTextLabel.setVisible(showChoiceWidgets);
		((GridData)validationText.getLayoutData()).exclude = !showChoiceWidgets;
		((GridData)validationTextLabel.getLayoutData()).exclude = !showChoiceWidgets;
		
		section.layout(true);
		Point size = section.getSize();
		Point newsize = section.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		section.setSize(size.x, newsize.y);
	}
	
	protected void validationChange(String text) {
		String[] newParams = text.split("\n");
		
		input.getValidValues().clear();
		input.getValidValues().addAll(Arrays.asList(newParams));
		
		defaultCombo.setItems(newParams);
	}
	
	protected void descriptionChange(String text) {
		input.setDescription(text);
	}
}
