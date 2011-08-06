package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.library.StatusCode;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class ConfigurationBlock extends AbstractBlock {

	public enum OperationType {
		DEPLOY, UPDATE, AUTO_DEPLOY
	}

	private Combo deployCombo;
	private IZendTarget[] deployComboTargets = new IZendTarget[0];
	private Link targetLink;
	private Text baseUrl;
	private Text userAppName;
	private Button ignoreFailures;
	private Button deployButton;
	private Button updateButton;
	private Button syncButton;

	private TargetsManager targetsManager;
	private IWizard wizard;
	private Combo applicationSelectionCombo;
	private ApplicationInfo[] applicationInfos = new ApplicationInfo[0];
	private OperationType defaultOperation;

	public ConfigurationBlock(IStatusChangeListener context) {
		this(context, OperationType.DEPLOY, null);
	}

	public ConfigurationBlock(IStatusChangeListener context, OperationType defaultOp, IWizard wizard) {
		super(context);
		this.targetsManager = TargetsManagerService.INSTANCE.getTargetManager();
		this.wizard = wizard;
		this.defaultOperation = defaultOp;
	}

	@Override
	public Composite createContents(Composite parent) {
		super.createContents(parent);
		getContainer().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		createDeployCombo(getContainer());
		createLocationLink(getContainer());
		baseUrl = createLabelWithText(Messages.parametersPage_baseURL, "", getContainer());
		ExpandableComposite expComposite = new ExpandableComposite(getContainer(), SWT.NONE,
				ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		expComposite.setText("Advanced Settings");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		expComposite.setLayoutData(gd);
		Composite advancedSection = new Composite(expComposite, SWT.NONE);
		advancedSection.setLayout(new GridLayout(2, false));
		createOperationsSection(advancedSection);
		userAppName = createLabelWithText(Messages.parametersPage_appUserName,
				Messages.parametersPage_appUserNameTooltip, advancedSection);
		ignoreFailures = createLabelWithCheckbox(Messages.parametersPage_ignoreFailures,
				Messages.parametersPage_ignoreFailuresTooltip, advancedSection);
		;
		expComposite.setClient(advancedSection);
		expComposite.setExpanded(true);
		return getContainer();
	}

	@Override
	public void initializeFields(IDeploymentHelper helper) {
		for (int i = 0; i < deployComboTargets.length; i++) {
			if (deployComboTargets[i].getId().equals(helper.getTargetId())) {
				deployCombo.select(i);
			}
		}
		URL newBaseURL = helper.getBaseURL();
		if (newBaseURL != null) {
			if (helper.isDefaultServer()) {
				String targetHost = getTarget().getDefaultServerURL().toString();
				baseUrl.setText(targetHost + newBaseURL.getPath());
			} else {
				baseUrl.setText(newBaseURL.toString());
			}
		}
		ignoreFailures.setSelection(helper.isIgnoreFailures());
		userAppName.setText(helper.getAppName());
	}

	public void initDefaultOperation() {
		switch (defaultOperation) {
		case DEPLOY:
			deployButton.setSelection(true);
			break;
		case UPDATE:
			updateButton.setSelection(true);
			enableApplicationSelectionSection(true);
			break;
		case AUTO_DEPLOY:
			syncButton.setSelection(true);
			break;
		default:
			break;
		}
	}

	@Override
	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_TargetLocation);
		}
		if (getBaseURL() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_BaseUrl);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.deploymentWizard_Message);
	}

	public OperationType getOperationType() {
		if (deployButton.getSelection()) {
			return OperationType.DEPLOY;
		}
		if (updateButton.getSelection()) {
			return OperationType.UPDATE;
		}
		return OperationType.AUTO_DEPLOY;
	}

	public ApplicationInfo getApplicationToUpdate() {
		if (wizard != null) {
			int idx = applicationSelectionCombo.getSelectionIndex();
			if (idx <= -1) {
				return null;
			}
			return applicationInfos[idx];
		}
		return null;
	}

	public URL getBaseURL() {
		try {
			URL result = new URL(baseUrl.getText());
			return result;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public String getUserAppName() {
		return userAppName.getText();
	}

	public boolean isDefaultServer() {
		URL baseUrl = getBaseURL();
		URL targetUrl = getTarget().getHost();
		if (baseUrl.getHost().equals(targetUrl.getHost())) {
			return true;
		}
		return false;
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

	public void setBaseURLEnabled(boolean value) {
		baseUrl.setEnabled(value);
	}

	public void setDeployComboEnabled(boolean value) {
		deployCombo.setEnabled(value);
		targetLink.setEnabled(value);
	}

	public void setUserAppNameEnabled(boolean value) {
		userAppName.setEnabled(value);
	}

	public void setIgnoreFailuresEnabled(boolean value) {
		ignoreFailures.setEnabled(value);
	}

	private void createLocationLink(Composite container) {
		targetLink = new Link(container, SWT.NONE);
		String text = "<a>" + Messages.parametersPage_AddTarget + "</a>";
		targetLink.setText(text);
		targetLink.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new AddTargetAction().run();
			}
		});
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 2;
		targetLink.setLayoutData(gd);
	}

	private void createDeployCombo(Composite container) {
		deployCombo = createLabelWithCombo(Messages.parametersPage_DeployTo, "", container);
		deployCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IZendTarget selectedTarget = deployComboTargets[deployCombo.getSelectionIndex()];
				changeHost(selectedTarget);
			}
		});
		populateTargetsList();
	}

	private void createOperationsSection(Composite parent) {
		Group container = new Group(parent, SWT.NONE);
		container.setText("Operation");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		container.setLayoutData(gd);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		container.setLayout(layout);

		Label operationLabel = new Label(container, SWT.NONE);
		operationLabel.setText("Choose operation which should be performed:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		operationLabel.setLayoutData(gd);

		deployButton = createRadioButton(container, "Deploy");
		deployButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableApplicationSelectionSection(false);
			}
		});

		updateButton = createRadioButton(container, "Update");
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (updateButton.getSelection()) {
					enableApplicationSelectionSection(true);
				}
			}
		});
		deployCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (updateButton.getSelection()) {
					getApplicationsInfo();
				}
			}
		});

		applicationSelectionCombo = createLabelWithCombo("Choose application to update:", "",
				container);
		applicationSelectionCombo.setEnabled(false);
		applicationSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillFieldsByAppInfo();
			}
		});

		syncButton = createRadioButton(container, "Automatic Deploy");
		syncButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableApplicationSelectionSection(false);
			}
		});
	}

	private void fillFieldsByAppInfo() {
		int index = applicationSelectionCombo.getSelectionIndex();
		if (index != -1) {
			ApplicationInfo info = applicationInfos[index];
			IDeploymentHelper helper = new DeploymentHelper();
			try {
				helper.setAppId(info.getId());
				helper.setAppName(info.getUserAppName());
				URL baseURL = new URL(info.getBaseUrl());
				if (baseURL.getHost().equals(BaseUrlControl.DEFAULT_HOST)) {
					helper.setDefaultServer(true);
					IZendTarget target = getTarget();
					URL updatedURL = new URL(baseURL.getProtocol(), target.getHost().getHost(),
							baseURL.getPath());
					helper.setBaseURL(updatedURL.toString());
				} else {
					helper.setDefaultServer(false);
					helper.setBaseURL(baseURL.toString());
				}
				initializeFields(helper);
				setBaseURLEnabled(false);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
	}

	private Button createRadioButton(Group container, String label) {
		Button button = new Button(container, SWT.RADIO);
		button.setText(label);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		return button;
	}

	private void enableApplicationSelectionSection(boolean value) {
		if (value) {
			applicationSelectionCombo.setEnabled(true);
			getApplicationsInfo();
		} else {
			initializeFields(new DeploymentHelper());
		}
		setBaseURLEnabled(!value);
		setUserAppNameEnabled(!value);
	}

	private void getApplicationsInfo() {
		final IZendTarget selectedTarget = getTarget();
		if (selectedTarget != null) {
			try {
				wizard.getContainer().run(true, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						StatusChangeListener listener = new StatusChangeListener(monitor);
						ZendApplication app = new ZendApplication(new EclipseMappingModelLoader());
						app.addStatusChangeListener(listener);
						applicationInfos = new ApplicationInfo[0];
						ApplicationsList info = app.getStatus(selectedTarget.getId());
						org.zend.sdklib.library.IStatus status = listener.getStatus();
						StatusCode code = status.getCode();
						if (code == StatusCode.ERROR) {
							StatusManager.getManager().handle(new SdkStatus(status),
									StatusManager.SHOW);
						} else {
							setApplicationsInfos(info);
						}
					}
				});
				populateApplicationsList();
			} catch (InvocationTargetException e) {
				Activator.log(e);
			} catch (InterruptedException e) {
				Activator.log(e);
			}
		}
	}

	private void setApplicationsInfos(ApplicationsList list) {
		if (list != null && list.getApplicationsInfo() != null) {
			applicationInfos = list.getApplicationsInfo().toArray(new ApplicationInfo[0]);
		}
	}

	private void populateApplicationsList() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				applicationSelectionCombo.removeAll();
				if (applicationInfos.length != 0) {
					for (ApplicationInfo info : applicationInfos) {
						applicationSelectionCombo.add(info.getAppName() + " (name: "
								+ info.getUserAppName() + ")");
					}
				}
				if (applicationSelectionCombo.getItemCount() > 0) {
					if (defaultOperation == OperationType.UPDATE) {
						URL url = getBaseURL();
						String stringUrl = url.toString();
						if (isDefaultServer()) {
							stringUrl = stringUrl.replaceFirst("default",
									BaseUrlControl.DEFAULT_HOST);
						}
						for (int i = 0; i < applicationInfos.length; i++) {

							if (applicationInfos[i].getBaseUrl().equals(stringUrl)) {
								applicationSelectionCombo.select(i);
							}
						}
					} else {
						applicationSelectionCombo.select(0);
						fillFieldsByAppInfo();
					}
				}
			}
		});
	}

	private void populateTargetsList() {
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

	private void changeHost(IZendTarget target) {
		URL targetHost = target.getHost();
		URL oldUrl = getBaseURL();
		try {
			URL updatedUrl = new URL(targetHost.getProtocol(), targetHost.getHost(),
					targetHost.getPort(), oldUrl.getFile());
			baseUrl.setText(updatedUrl.toString());
		} catch (MalformedURLException e) {
			Activator.log(e);
		}
	}

}
