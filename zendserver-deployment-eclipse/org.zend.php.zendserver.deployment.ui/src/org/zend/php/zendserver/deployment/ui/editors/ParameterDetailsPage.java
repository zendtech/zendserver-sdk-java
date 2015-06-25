package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
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
	private ListField validation;
	private ComboField identical;

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
		
		refreshParametersList();
		showChoiceWidgets();
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
		
		IMessageManager mmng = mform.getMessageManager();
		
		EditorField id = fields.add(new TextField(null, DeploymentDescriptorPackage.ID, Messages.ParameterDetailsPage_Id, mmng));
		id.create(client, toolkit);
		((GridData)id.getText().getLayoutData()).widthHint = 100;
		
		ComboField type = (ComboField) fields.add(new ComboField(null, DeploymentDescriptorPackage.TYPE, Messages.ParameterDetailsPage_Type, SWT.READ_ONLY));
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
		
		EditorField display = fields.add(new TextField(null, DeploymentDescriptorPackage.DISPLAY, Messages.ParameterDetailsPage_Display, mmng));
		display.create(client, toolkit);
		((GridData)display.getText().getLayoutData()).widthHint = 100;
		
		defaultValue = fields.add(new TextField(null, DeploymentDescriptorPackage.DEFAULTVALUE, Messages.ParameterDetailsPage_DefaultValue, mmng));
		defaultValue.create(client, toolkit);
		((GridData)defaultValue.getText().getLayoutData()).widthHint = 100;
		
		defaultCombo = fields.add(new ComboField(null, DeploymentDescriptorPackage.DEFAULTVALUE, Messages.ParameterDetailsPage_DefaultValue));
		defaultCombo.create(client, toolkit);
		((GridData)defaultCombo.getText().getLayoutData()).widthHint = 100;
		
		validation = (ListField) fields.add(new ListField(null, DeploymentDescriptorPackage.VALIDATION, Messages.ParameterDetailsPage_ValidValues, mmng));
		validation.create(client, toolkit);
		((GridData)validation.getText().getLayoutData()).heightHint = 100;
		((GridData)validation.getText().getLayoutData()).widthHint = 100;
		
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
							validationChange();
						};
					});
				}
			}
		});
		
		EditorField description = fields.add(new TextField(null, DeploymentDescriptorPackage.PARAM_DESCRIPTION, Messages.ParameterDetailsPage_Description, mmng));
		description.create(client, toolkit);
		GridData gd = ((GridData)description.getText().getLayoutData());
		gd.heightHint = 100;
		gd.widthHint = 100;
		
		toolkit.paintBordersFor(client);
	}
	
	private void refreshParametersList() {
		List<IParameter> params = editor.getDescriptorContainer().getDescriptorModel().getParameters();
		
		String[] items = new String[params.size()];
		for (int i = 0; i < params.size(); i++) {
			IParameter param = params.get(i);
			String paramId = param.getId();
			items[i] = paramId;
		}
		
		String[] oldItems = identical.getCombo().getItems();
		if (!Arrays.equals(oldItems, items)) {
			identical.setItems(items);
		}
	}
	
	private void showChoiceWidgets() {
		boolean showChoiceWidgets = IParameter.CHOICE.equals(((IParameter)input).getType());
		
		defaultValue.setVisible(!showChoiceWidgets);
		defaultCombo.setVisible(showChoiceWidgets);
		validation.setVisible(showChoiceWidgets);
		
		section.layout(true);
		Point size = section.getSize();
		Point newsize = section.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		section.setSize(size.x, newsize.y);
	}
	
	protected void validationChange() {
		List<String> vals = ((IParameter)input).getValidValues();
		String[] strings = vals.toArray(new String[vals.size()]);
		
		((ComboField)defaultCombo).setItems(strings);
	}
}
