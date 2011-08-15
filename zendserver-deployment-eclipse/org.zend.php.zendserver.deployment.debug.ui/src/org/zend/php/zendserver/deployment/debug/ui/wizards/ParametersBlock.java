package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.ParameterType;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

public class ParametersBlock extends AbstractBlock {

	private class DeploymentParameter {

		private Control control;
		private IParameter parameter;
		private String name;
		private ParameterType type;

		private DeploymentParameter(IParameter parameter, String name, ParameterType type) {
			this.parameter = parameter;
			this.name = name;
			this.type = type;
		}

		public String getValue() {
			switch (type) {
			case STRING:
			case EMAIL:
			case PASSWORD:
				return ((Text) control).getText();
			case CHOICE:
				return ((Combo) control).getText();
			case CHECKBOX:
				return String.valueOf(((Button) control).getSelection());
			default:
				return ""; //$NON-NLS-1$
			}
		}

		public void setValue(Object value) {
			switch (type) {
			case STRING:
			case EMAIL:
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

		public void createControl(Composite composite) {
			String tooltip = parameter.getDescription();
			switch (type) {
			case STRING:
			case EMAIL:
				control = createLabelWithText(name, tooltip, composite);
				if (parameter.getDefaultValue() != null) {
					((Text) control).setText(parameter.getDefaultValue());
				}
				break;
			case PASSWORD:
				control = createLabelWithText(name, tooltip, composite);
				((Text) control).setEchoChar('*');
				if (parameter.getDefaultValue() != null) {
					((Text) control).setText(parameter.getDefaultValue());
				}
				break;
			case CHOICE:
				control = createLabelWithCombo(name, tooltip, composite);
				List<String> values = parameter.getValidValues();
				((Combo) control).setItems(values.toArray(new String[values.size()]));
				break;
			case CHECKBOX:
				control = createLabelWithCheckbox(name, tooltip, composite);
				if (parameter.getDefaultValue() != null) {
					((Button) control).setSelection(Boolean.valueOf(parameter.getDefaultValue()));
				}
				break;
			default:
				break;
			}
		}
	}

	private List<DeploymentParameter> parameters;
	private IDescriptorContainer model;
	private Group parametersGroup;

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
		createParameterGroups(getContainer());
	}

	@Override
	public IDeploymentHelper getHelper() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (DeploymentParameter param : parameters) {
			result.put(param.getId(), param.getValue());
		}
		DeploymentHelper helper = new DeploymentHelper();
		helper.setUserParams(result);
		return helper;
	}

	@Override
	public Composite createContents(Composite parent) {
		super.createContents(parent);
		getContainer().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, param.getParameter()
						.getDisplay() + Messages.parametersPage_ValidationError_ParamRequired);
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.parametersPage_Description);
	}

	private void createParameterGroups(Composite container) {
		parametersGroup = new Group(container, SWT.NULL);
		parametersGroup.setText(Messages.parametersPage_applicationParams);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		parametersGroup.setLayoutData(gd);
		parametersGroup.setLayout(new FillLayout(SWT.FILL));
		if (model.getDescriptorModel() != null) {
			final ScrolledComposite scrollComposite = new ScrolledComposite(parametersGroup,
					SWT.V_SCROLL);
			scrollComposite.setLayout(new FillLayout());
			final Composite parent = new Composite(scrollComposite, SWT.NONE);
			parent.setLayout(new GridLayout(2, false));
			scrollComposite.setExpandVertical(true);
			scrollComposite.setExpandHorizontal(true);
			scrollComposite.setContent(parent);
			scrollComposite.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					Rectangle r = scrollComposite.getClientArea();
					scrollComposite.setMinSize(parent.computeSize(r.width, SWT.DEFAULT));
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
			parameter = new DeploymentParameter(category.getParameter(), category.getName(), type);
			parameter.createControl(parent);
			parameters.add(parameter);
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
