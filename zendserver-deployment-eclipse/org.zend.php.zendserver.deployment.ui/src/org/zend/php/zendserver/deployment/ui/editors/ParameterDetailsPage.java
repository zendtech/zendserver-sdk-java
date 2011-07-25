package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.ui.Messages;


public class ParameterDetailsPage extends DescriptorDetailsPage {
	
	private IManagedForm mform;
	
	private EditorField defaultValue;
	private EditorField defaultCombo;
	private Text validationText;
	private ComboField identical;

	private boolean isRefresh;

	private Label validationTextLabel;

	private Section section;
	
	public ParameterDetailsPage(DeploymentDescriptorEditor editor) {
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

	public void refresh() {
		super.refresh();
		
		isRefresh = true;
		try {
			List<String> validValues = ((IParameter)input).getValidValues();
			if (validValues != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < validValues.size(); i++) {
					sb.append(validValues.get(i)).append("\n"); //$NON-NLS-1$
				}
				validationText.setText(sb.toString());
			} else {
				validationText.setText(""); //$NON-NLS-1$
			}
			refreshParametersList();
			showChoiceWidgets();
		} finally {
			isRefresh = false;
		}
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
		section.setText(Messages.ParameterDetailsPage_ParamDetails);
		section.marginWidth = 5;
		section.marginHeight = 5;
		
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(3, false));
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		section.setClient(client);
		
		EditorField id = fields.add(new TextField(null, DeploymentDescriptorPackage.ID, Messages.ParameterDetailsPage_Id));
		id.create(client, toolkit);
		((GridData)id.getText().getLayoutData()).widthHint = 100;
		
		ComboField type = (ComboField) fields.add(new ComboField(null, DeploymentDescriptorPackage.TYPE, Messages.ParameterDetailsPage_Type));
		type.setItems(new String[] {
				IParameter.CHOICE,
				IParameter.STRING,
				IParameter.PASSWORD,
				IParameter.EMAIL,
				IParameter.CHECKBOX,
				IParameter.NUMBER,
				IParameter.HOSTNAME
		});
		type.create(client, toolkit);
		((GridData)type.getCombo().getLayoutData()).widthHint = 100;
		
		toolkit.createLabel(client, ""); //$NON-NLS-1$
		
		EditorField required = (CheckboxField) fields.add(new CheckboxField(null, DeploymentDescriptorPackage.REQUIRED, Messages.ParameterDetailsPage_Required));
		required.create(client, toolkit);
		
		EditorField display = fields.add(new TextField(null, DeploymentDescriptorPackage.DISPLAY, Messages.ParameterDetailsPage_Display));
		display.create(client, toolkit);
		((GridData)display.getText().getLayoutData()).widthHint = 100;
		
		defaultValue = fields.add(new TextField(null, DeploymentDescriptorPackage.DEFAULTVALUE, Messages.ParameterDetailsPage_DefaultValue));
		defaultValue.create(client, toolkit);
		((GridData)defaultValue.getText().getLayoutData()).widthHint = 100;
		
		defaultCombo = fields.add(new ComboField(null, DeploymentDescriptorPackage.DEFAULTVALUE, Messages.ParameterDetailsPage_DefaultValue));
		defaultCombo.create(client, toolkit);
		
		validationTextLabel = toolkit.createLabel(client, Messages.ParameterDetailsPage_ValidValues);
		validationTextLabel.setLayoutData(new GridData());
		validationText = toolkit.createText(client, "", SWT.MULTI|SWT.WRAP|SWT.V_SCROLL); //$NON-NLS-1$
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
		
		toolkit.createLabel(client, ""); //$NON-NLS-1$
		
		EditorField readonly = (CheckboxField) fields.add(new CheckboxField(null, DeploymentDescriptorPackage.READONLY, Messages.ParameterDetailsPage_Readonly));
		readonly.create(client, toolkit);
		
		identical = (ComboField) fields.add(new ComboField(null, DeploymentDescriptorPackage.IDENTICAL, Messages.ParameterDetailsPage_Identical));
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
		
		EditorField description = fields.add(new TextField(null, DeploymentDescriptorPackage.PARAM_DESCRIPTION, Messages.ParameterDetailsPage_Description));
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
		boolean showChoiceWidgets = IParameter.CHOICE.equals(((IParameter)input).getType());
		
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
		String[] newParams = text.split("\n"); //$NON-NLS-1$
		
		((IParameter)input).getValidValues().clear();
		((IParameter)input).getValidValues().addAll(Arrays.asList(newParams));
		
		((ComboField)defaultCombo).setItems(newParams);
	}
}
