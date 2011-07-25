package org.zend.php.zendserver.deployment.debug.ui.dialogs;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.ParameterType;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class DeploymentConfigurationBlock {

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
				return "";
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

	private IDescriptorContainer model;

	private Combo deployCombo;
	private IZendTarget[] deployComboTargets = new IZendTarget[0];
	private Link targetLocation;
	private BaseUrlControl baseUrl;
	private Text userAppName;
	private Button defaultServer;
	private Button ignoreFailures;

	private List<DeploymentParameter> parameters;
	private TargetsManager targetsManager;

	private List<Listener> listeners;

	private IStatusChangeListener context;

	public DeploymentConfigurationBlock(IProject project, IStatusChangeListener context) {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		this.model = DescriptorContainerManager.getService().openDescriptorContainer(
				(IFile) descriptor);
		this.targetsManager = TargetsManagerService.INSTANCE.getTargetManager();
		this.parameters = new ArrayList<DeploymentParameter>();
		this.context = context;
		listeners = new ArrayList<Listener>();
	}

	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createDeployCombo(container);
		createLocationLink(container);
		userAppName = createLabelWithText(Messages.parametersPage_appUserName,
				Messages.parametersPage_appUserNameTooltip, container);
		createBaseUrl(container);
		ignoreFailures = createLabelWithCheckbox(Messages.parametersPage_ignoreFailures,
				Messages.parametersPage_ignoreFailuresTooltip, container);
		createParameterGroups(container);
		return container;
	}

	public HashMap<String, String> getParameters() {
		HashMap<String, String> result = new HashMap<String, String>();
		for (DeploymentParameter param : parameters) {
			result.put(param.getId(), param.getValue());
		}
		return result;
	}

	public URL getBaseURL() {
		URL result = baseUrl.getURL();
		return result != null ? result : null;
	}

	public String getUserAppName() {
		return userAppName.getText();
	}

	public boolean isDefaultServer() {
		return defaultServer.getSelection();
	}

	public boolean isIgnoreFailures() {
		return ignoreFailures.getSelection();
	}

	public IZendTarget getTarget() {
		int idx = deployCombo.getSelectionIndex();
		if (idx <= -1) {
			return null;
		}
		IZendTarget target = deployComboTargets[idx];
		return targetsManager.getTargetById(target.getId());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	private void createBaseUrl(Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.parametersPage_baseURL);
		baseUrl = new BaseUrlControl();
		baseUrl.createControl(container);
		baseUrl.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				context.statusChanged(validatePage());
			}
		});
		baseUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		defaultServer = createLabelWithCheckbox(Messages.parametersPage_defaultServer,
				Messages.parametersPage_defaultServerTooltip, container);
		defaultServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				baseUrl.setDefaultServer(defaultServer.getSelection());
				context.statusChanged(validatePage());
			}
		});
	}

	private void createParameterGroups(Composite container) {
		Group paramsGroup = new Group(container, SWT.NULL);
		paramsGroup.setText(Messages.parametersPage_applicationParams);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		paramsGroup.setLayoutData(gd);
		paramsGroup.setLayout(new FillLayout(SWT.FILL));
		if (model.getDescriptorModel() != null) {
			final ScrolledComposite scrollComposite = new ScrolledComposite(paramsGroup,
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

	private void createLocationLink(Composite container) {
		targetLocation = new Link(container, SWT.NONE);
		String text = "<a>" + Messages.parametersPage_TargetLocation + "</a>";
		targetLocation.setText(text);
		targetLocation.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// TODO add link to target management
			}
		});
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 2;
		targetLocation.setLayoutData(gd);
	}

	private Combo createLabelWithCombo(String labelText, String tooltip, Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		Combo combo = new Combo(container, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				context.statusChanged(validatePage());
			}
		});
		combo.setToolTipText(tooltip);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	private Text createLabelWithText(String labelText, String tooltip, Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(labelText);
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				context.statusChanged(validatePage());
			}
		});
		text.setToolTipText(tooltip);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private Button createLabelWithCheckbox(String desc, String tooltip, Composite composite) {
		Button button = new Button(composite, SWT.CHECK);
		button.setText(desc);
		button.setToolTipText(tooltip);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		return button;
	}

	private void createDeployCombo(Composite container) {
		deployCombo = createLabelWithCombo(Messages.parametersPage_DeployTo, "", container);
		populateLocationList();
	}

	private void populateLocationList() {
		deployComboTargets = targetsManager.getTargets();
		deployCombo.removeAll();
		String defaultId = targetsManager.getDefaultTargetId();
		int defaultNo = 0;

		if (deployComboTargets.length != 0) {
			int i = 0;
			for (IZendTarget target : deployComboTargets) {
				if (target.getId().equals(defaultId)) {
					defaultNo = i;
				}
				deployCombo.add(target.getHost() + " (Id: " + target.getId() + ")");
				i++;
			}
		}
		if (deployCombo.getItemCount() > 0) {
			deployCombo.select(defaultNo);
		}
	}

	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_TargetLocation);
		}
		if (baseUrl.isValid()) {
			for (DeploymentParameter param : parameters) {
				if (param.getParameter().isRequired() && param.getValue().isEmpty()) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, param.getParameter()
							.getDisplay() + " is required.");
				}
			}
		} else {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_BaseUrl);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.deploymentDialog_Message);
	}

}
