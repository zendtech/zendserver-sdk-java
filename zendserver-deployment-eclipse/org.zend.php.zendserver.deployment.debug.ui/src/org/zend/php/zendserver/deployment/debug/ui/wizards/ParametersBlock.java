package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.ParameterType;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

public class ParametersBlock extends AbstractBlock {

	private class DeploymentParameter {

		private Control control;
		private IParameter parameter;
		private String name;
		private ParameterType type;
		private IParameterValidator validator;

		private DeploymentParameter(IParameter parameter, String name, ParameterType type) {
			this.parameter = parameter;
			this.name = name;
			this.type = type;
		}

		private IParameterValidator getValidator() {
			switch (type) {
			case EMAIL:
				return new EmailValidator();
			case NUMBER:
				return new NumberValidator();
			case PASSWORD:
				return new PasswordValidator(ParametersBlock.this, parameter.getIdentical());
			default:
				return null;
			}
		}

		public void initValidator() {
			this.validator = getValidator();
		}

		public String getValue() {
			switch (type) {
			case STRING:
			case EMAIL:
			case NUMBER:
			case HOSTNAME:
			case PASSWORD:
				return ((Text) control).getText();
			case CHOICE:
				return ((Combo) control).getText();
			case CHECKBOX:
				return ((Button) control).getSelection() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
			default:
				return ""; //$NON-NLS-1$
			}
		}

		public void setValue(Object value) {
			switch (type) {
			case STRING:
			case EMAIL:
			case NUMBER:
			case HOSTNAME:
			case PASSWORD:
				((Text) control).setText(String.valueOf(value));
				break;
			case CHOICE:
				((Combo) control).setText(String.valueOf(value));
				break;
			case CHECKBOX:
				((Button) control).setSelection(Boolean.valueOf(String.valueOf(value)));
				break;
			default:
			}
		}

		public IParameter getParameter() {
			return parameter;
		}

		public String getId() {
			return parameter.getId();
		}

		public String getName() {
			return name;
		}

		public void createControl(Composite composite) {
			String tooltip = parameter.getDescription();
			switch (type) {
			case STRING:
			case HOSTNAME:
			case NUMBER:
			case EMAIL:
				control = createLabelWithText(getName(name), tooltip, composite,
						parameter.isRequired());
				if (parameter.getDefaultValue() != null) {
					((Text) control).setText(parameter.getDefaultValue());
				}
				break;
			case PASSWORD:
				control = createLabelWithText(getName(name), tooltip, composite,
						parameter.isRequired());
				((Text) control).setEchoChar('*');
				if (parameter.getDefaultValue() != null) {
					((Text) control).setText(parameter.getDefaultValue());
				}
				break;
			case CHOICE:
				control = createLabelWithCombo(getName(name), tooltip,
						composite, parameter.isRequired(),
						parameter.isReadOnly());
				List<String> values = parameter.getValidValues();
				((Combo) control).setItems(values.toArray(new String[values.size()]));
				if (parameter.getDefaultValue() != null) {
					String defaultValue = parameter.getDefaultValue();
					for (int i = 0; i < values.size(); i++) {
						if (values.get(i).equals(defaultValue)) {
							((Combo) control).select(i);
						}
					}
				}
				break;
			case CHECKBOX:
				control = createLabelWithCheckbox(name, tooltip, composite);
				if (parameter.getDefaultValue() != null) {
					((Button) control).setSelection(Boolean.valueOf(parameter.getDefaultValue()));
				}
				break;
			default:
				return;
			}
			if (parameter.isReadOnly()) {
				control.setEnabled(false);
			}
		}

		public IStatus validate() {
			if (validator != null) {
				return validator.validate(getName(), getValue());
			}
			return Status.OK_STATUS;
		}

		private String getName(String name) {
			return name + ":"; //$NON-NLS-1$
		}

	}

	private List<DeploymentParameter> parameters;
	private IDescriptorContainer model;
	private ScrolledComposite parametersGroup;
	private Composite paramsSection;
	private Button exportButton;

	public ParametersBlock(IStatusChangeListener context) {
		super(context);
		this.parameters = new ArrayList<DeploymentParameter>();
	}

	public void createParametersGroup(IProject project) {
		if (parametersGroup != null) {
			parametersGroup.dispose();
			parameters.clear();
		}
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.model = DescriptorContainerManager.getService().openDescriptorContainer(
				(IFile) descriptor);
		createParameterGroups(paramsSection);
		for (DeploymentParameter param : parameters) {
			param.initValidator();
		}
	}

	@Override
	public IDeploymentHelper getHelper() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (DeploymentParameter param : parameters) {
			String value = param.getValue();
			if (value == null || value.isEmpty()) {
				value = param.getParameter().getDefaultValue();
			}
			if (value == null) {
				value = ""; //$NON-NLS-1$
			}
			result.put(param.getId(), value);
		}
		DeploymentHelper helper = new DeploymentHelper();
		helper.setUserParams(result);
		return helper;
	}

	@Override
	public Composite createContents(Composite parent) {
		super.createContents(parent);
		getContainer().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		paramsSection = new Composite(getContainer(), SWT.NONE);
		paramsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		paramsSection.setLayout(layout);
		createButtonSection();
		return getContainer();
	}

	@Override
	public void initializeFields(IDeploymentHelper helper) {
		Map<String, String> params = helper.getUserParams();
		for (DeploymentParameter parameter : parameters) {
			String value = params.get(parameter.getParameter().getId());
			if (value != null) {
				parameter.setValue(value);
			}
		}
	}

	@Override
	public IStatus validatePage() {
		for (DeploymentParameter param : parameters) {
			if (param.getParameter().isRequired() && param.getValue().isEmpty()) {
				exportButton.setEnabled(false);
				return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
						param.getName());
			}
			IStatus status = param.validate();
			if (status.getSeverity() == IStatus.ERROR) {
				return status;
			}
		}
		exportButton.setEnabled(true);
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.parametersPage_Description);
	}

	private void createButtonSection() {
		Composite buttonSection = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonSection.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 100;
		buttonSection.setLayoutData(gd);
		Button importButton = createButton(buttonSection, Messages.ParametersBlock_ImportButton);
		importButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importParameters(e.widget.getDisplay().getActiveShell());
			}
		});
		exportButton = createButton(buttonSection, Messages.ParametersBlock_ExportButton);
		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportParameters(e.widget.getDisplay().getActiveShell());
			}
		});
	}

	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return button;
	}

	private void importParameters(Shell shell) {
		FileDialog dialog = new FileDialog(shell);
		dialog.setFileName("params.properties"); //$NON-NLS-1$
		dialog.setFilterPath(model.getFile().getParent().getLocation().toOSString());
		dialog.setText(Messages.ParametersBlock_ImportDialogDescription);
		dialog.setFilterExtensions(new String[] { "*" }); //$NON-NLS-1$
		String selection = dialog.open();
		if (selection == null) {
			return;
		}
		File selectedFile = new File(selection);
		if (selectedFile != null && !selectedFile.isDirectory() && selectedFile.exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(selectedFile));
				Map<String, String> userParams = new HashMap<String, String>();
				Enumeration<?> names = props.propertyNames();
				if (names == null) {
					return;
				}
				while (names.hasMoreElements()) {
					String key = (String) names.nextElement();
					String value = (String) props.getProperty(key);
					userParams.put(key, value);
				}
				IDeploymentHelper helper = new DeploymentHelper();
				helper.setUserParams(userParams);
				initializeFields(helper);
				listener.statusChanged(validatePage());
			} catch (IOException e) {
				Activator.log(e);
			}
		}
	}

	private void exportParameters(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText(Messages.ParametersBlock_ExportDialogDescription);
		dialog.setFileName("params.properties"); //$NON-NLS-1$
		dialog.setFilterPath(model.getFile().getParent().getLocation().toOSString());
		dialog.setFilterExtensions(new String[] { "*" }); //$NON-NLS-1$
		dialog.setOverwrite(true);

		final String destination = dialog.open();
		if (destination == null || destination.isEmpty()) {
			return;
		}
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			public void run() {
				Properties props = new Properties();
				Map<String, String> params = getHelper().getUserParams();
				Set<Entry<String, String>> paramsSet = params.entrySet();
				for (Entry<String, String> param : paramsSet) {
					props.put(param.getKey(), param.getValue());
				}
				try {
					OutputStream out = new FileOutputStream(new File(destination));
					props.store(out, "deployment parameters"); //$NON-NLS-1$
					out.close();
				} catch (IOException e) {
					Activator.log(e);
				}

			}
		});
	}

	private void createParameterGroups(Composite container) {
		parametersGroup = new ScrolledComposite(container, SWT.V_SCROLL);
		if (model.getDescriptorModel() != null) {
			parametersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			parametersGroup.setLayout(new FillLayout());
			final Composite parent = new Composite(parametersGroup, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.horizontalSpacing = 0;
			parent.setLayout(layout);
			parametersGroup.setExpandVertical(true);
			parametersGroup.setExpandHorizontal(true);
			parametersGroup.setContent(parent);
			parametersGroup.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					Rectangle r = parametersGroup.getClientArea();
					parametersGroup.setMinSize(parent.computeSize(r.width, SWT.DEFAULT));
				}
			});
			List<ParametersCategory> categories = createCategories(model.getDescriptorModel()
					.getParameters());
			for (ParametersCategory category : categories) {
				createGroup(category, parent);
			}
		}
	}

	private void createGroup(ParametersCategory category, Composite parent) {
		if (category.getParameter() == null) {
			Group paramsGroup = new Group(parent, SWT.NULL);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.horizontalSpan = 2;
			paramsGroup.setLayoutData(gd);
			paramsGroup.setText(category.getName());
			paramsGroup.setLayout(new GridLayout(2, true));
			List<ParametersCategory> categories = category.getCategories();
			for (ParametersCategory cat : categories) {
				createGroup(cat, paramsGroup);
			}
		} else {
			DeploymentParameter parameter = null;
			ParameterType type = ParameterType.byName(category.getParameter().getType());
			if (type != null) {
				parameter = new DeploymentParameter(category.getParameter(),
						category.getName(), type);
				parameter.createControl(parent);
				parameters.add(parameter);
			}
		}
	}

	private List<ParametersCategory> createCategories(List<IParameter> params) {
		List<ParametersCategory> categories = new ArrayList<ParametersCategory>();
		for (IParameter param : params) {
			List<String> labels = new ArrayList<String>(Arrays.asList(param.getDisplay().split(
					ParametersCategory.SEPARATOR)));
			ParametersCategory cat = createCategory(categories, labels, param);
			if (cat != null) {
				categories.add(cat);
			}
		}
		return categories;
	}

	private ParametersCategory createCategory(List<ParametersCategory> categories,
			List<String> labels, IParameter dp) {
		if (labels.size() > 1) {
			String catName = labels.remove(0);
			for (ParametersCategory category : categories) {
				if (category.getName().equals(catName)) {
					ParametersCategory cat = createCategory(category.getCategories(), labels, dp);
					if (cat != null) {
						category.addCategory(cat);
					}
					return null;
				}
			}
			ParametersCategory cat = new ParametersCategory(catName);
			cat.addCategory(createCategory(categories, labels, dp));
			return cat;
		} else {
			String catName = labels.remove(0);
			return new ParametersCategory(dp, catName);
		}
	}

}
