package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;
import org.zend.php.zendserver.deployment.ui.targets.TargetsCombo;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.progress.StatusCode;

public class ConfigurationBlock extends AbstractBlock {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private TargetsCombo targetsCombo = new TargetsCombo(true);
	private Text baseUrl;
	private Button ignoreFailures;
	private Button developmentMode;
	private Button warnUpdate;
	private Combo applicationNameCombo;
	private Button refreshButton;

	private IRunnableContext context;

	private ApplicationInfo[] applicationInfos = new ApplicationInfo[0];

	private String description;
	private String projectName;

	private List<String> bannedNames;
	private List<IDeployWizardContribution> contributions;

	public ConfigurationBlock(IStatusChangeListener listener,
			String projectName, IRunnableContext context, String description) {
		super(listener);
		this.context = context;
		this.description = description;
		this.bannedNames = LaunchUtils.getBannedNames();
		this.projectName = projectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * createContents(org.eclipse.swt.widgets.Composite, boolean)
	 */
	public Composite createContents(final Composite parent,
			final boolean resizeShell) {
		super.createContents(parent, resizeShell);
		getContainer().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));
		createDeployCombo(getContainer());
		baseUrl = createLabelWithText(Messages.configurationPage_baseURL,
				EMPTY_STRING, getContainer(), false);
		createApplicationSelection(getContainer());
		developmentMode = createLabelWithCheckbox(
				Messages.ConfigurationBlock_DevelopmentModeLabel, null,
				getContainer());
		Link devModeDesc = new Link(getContainer(), SWT.None);
		devModeDesc.setText(Messages.ConfigurationBlock_DevelopmentModeDesc);
		FontData fontData = devModeDesc.getFont().getFontData()[0];
		Font font = new Font(parent.getDisplay(), new FontData(
				fontData.getName(), fontData.getHeight(), SWT.ITALIC));
		devModeDesc.setFont(font);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		devModeDesc.setLayoutData(gd);
		devModeDesc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchHelpSystem help = PlatformUI.getWorkbench()
						.getHelpSystem();
				help.displayHelpResource(e.text);
			}
		});
		warnUpdate = createLabelWithCheckbox(
				Messages.ConfigurationBlock_WarnUpdatingLabel, null,
				getContainer());
		ignoreFailures = createLabelWithCheckbox(
				Messages.configurationPage_ignoreFailures,
				Messages.configurationPage_ignoreFailuresTooltip,
				getContainer());
		contributions = getContributions();
		for (IDeployWizardContribution contribution : contributions) {
			contribution
					.initialize(context, projectName, listener, description);
			contribution.createExtraSection(getContainer());
		}
		return getContainer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * initializeFields
	 * (org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper)
	 */
	public void initializeFields(IDeploymentHelper helper) {
		String targetId = helper.getTargetId();
		IZendTarget target = null;
		if ((targetId == null || targetId.isEmpty())) {
			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				targetsCombo.select(settings.get(DeploymentAttributes.TARGET_ID
						.getName()));
			}
		} else {
			targetsCombo.select(targetId);
			String applicationURL = LaunchUtils.getURLFromPreferences(helper
					.getProjectName());
			target = targetsCombo.getSelected();
			if (target != null && applicationURL != null) {
				baseUrl.setText(updateURL(target, applicationURL));
			}
		}
		IDialogSettings settings = getDialogSettings();
		if (helper.getAppId() == -1 && settings != null) {
			String developerModeVal = settings
					.get(DeploymentAttributes.DEVELOPMENT_MODE.getName());
			if (developerModeVal != null) {
				developmentMode.setSelection(Boolean.valueOf(developerModeVal));
			} else {
				developmentMode.setSelection(helper.isDevelopmentModeEnabled());
			}
			String warnUpdateVal = settings
					.get(DeploymentAttributes.WARN_UPDATE.getName());
			if (warnUpdateVal != null) {
				warnUpdate.setSelection(Boolean.valueOf(warnUpdateVal));
			} else {
				warnUpdate.setSelection(helper.isWarnUpdate());
			}
			String ignoreFailureVal = settings
					.get(DeploymentAttributes.IGNORE_FAILURES.getName());
			if (ignoreFailureVal != null) {
				ignoreFailures.setSelection(Boolean.valueOf(ignoreFailureVal));
			} else {
				ignoreFailures.setSelection(helper.isIgnoreFailures());
			}
		} else {
			developmentMode.setSelection(helper.isDevelopmentModeEnabled());
			warnUpdate.setSelection(helper.isWarnUpdate());
			ignoreFailures.setSelection(helper.isIgnoreFailures());
		}
		URL newBaseURL = helper.getBaseURL();
		if (baseUrl.getText().isEmpty() && newBaseURL != null) {
			if (target == null) {
				target = targetsCombo.getSelected();
			}
			if (target != null) {
				baseUrl.setText(updateURL(target, newBaseURL.toString()));
			}
		}
		applicationNameCombo.setText(helper.getAppName());
		for (IDeployWizardContribution c : contributions) {
			c.initializeFields(helper);
		}
	}

	public void clear() {
		baseUrl.setText(EMPTY_STRING);
		ignoreFailures.setSelection(true);
		applicationNameCombo.clearSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * validatePage()
	 */
	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.configurationPage_ValidationError_TargetLocation);
		}
		if (baseUrl != null && baseUrl.getText().isEmpty()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.ConfigurationBlock_UrlEmptyError);
		}
		URL baseUrl = null;
		try {
			baseUrl = getBaseURL();
			if (TargetsManager.isLocalhost(baseUrl.getHost())) {
				IPath file = new Path(baseUrl.getFile());
				if (file.segmentCount() > 0) {
					if (bannedNames.contains(file.segment(0))) {
						return new Status(
								IStatus.ERROR,
								Activator.PLUGIN_ID,
								Messages.ConfigurationBlock_LocalAppConflictErrorMessage);
					}
				}
			}
			String url = baseUrl.toString();
			if (url.contains(" ") || url.contains("\t")) { //$NON-NLS-1$ //$NON-NLS-2$
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.ConfigurationBlock_UrlWhitespacesError);
			}
		} catch (MalformedURLException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.configurationPage_ValidationError_BaseUrl);
		}
		for (IDeployWizardContribution c : contributions) {
			IStatus status = c.validate();
			if (status.getSeverity() != IStatus.OK) {
				return status;
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#getHelper
	 * ()
	 */
	public IDeploymentHelper getHelper() {
		DeploymentHelper helper = new DeploymentHelper();
		URL baseUrl = null;
		try {
			baseUrl = getBaseURL();
		} catch (MalformedURLException e) {
			// ignore, handled later
		}
		if (baseUrl != null) {
			helper.setBaseURL(baseUrl.toString());
		}
		if (getTarget() != null) {
			helper.setTargetId(getTarget().getId());
			helper.setTargetHost(getTarget().getHost().getHost());
		}
		ApplicationInfo selectedInfo = getAppicationNameSelection();
		if (selectedInfo != null) {
			helper.setAppId(selectedInfo.getId());
		}
		helper.setAppName(applicationNameCombo.getText());
		helper.setIgnoreFailures(ignoreFailures.getSelection());
		helper.setDefaultServer(isDefaultServer());
		if (selectedInfo != null && !warnUpdate.getSelection()) {
			helper.setOperationType(IDeploymentHelper.UPDATE);
		} else {
			helper.setOperationType(IDeploymentHelper.DEPLOY);
		}
		if (getInstalledLocation() != null) {
			helper.setInstalledLocation(getInstalledLocation());
		}
		if (warnUpdate.isEnabled()) {
			helper.setWarnUpdate(warnUpdate.getSelection());
		} else {
			helper.setWarnUpdate(false);
		}
		helper.setDevelopmentMode(developmentMode.getSelection());
		Map<String, String> extraAttributes = new HashMap<String, String>();
		for (IDeployWizardContribution c : contributions) {
			extraAttributes.putAll(c.getExtraAttributes());
		}
		helper.setExtraAtttributes(extraAttributes);
		return helper;
	}

	public void setBaseURLEnabled(boolean value) {
		baseUrl.setEnabled(value);
	}

	public void setDeployComboEnabled(boolean value) {
		targetsCombo.setEnabled(value);
	}

	public void setIgnoreFailuresEnabled(boolean value) {
		ignoreFailures.setEnabled(value);
	}

	public void setApplicationNameEnabled(boolean value) {
		applicationNameCombo.setEnabled(value);
		refreshButton.setEnabled(value);
	}

	public void setWarnUpdateEnabled(boolean value) {
		warnUpdate.setEnabled(value);
	}

	public void setDevelopmentModeEnabled(boolean value) {
		developmentMode.setEnabled(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#setEnabled
	 * (boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setBaseURLEnabled(enabled);
		setDeployComboEnabled(enabled);
		setIgnoreFailuresEnabled(enabled);
		setApplicationNameEnabled(enabled);
		setWarnUpdateEnabled(enabled);
		setDevelopmentModeEnabled(enabled);
	}

	private ApplicationInfo getAppicationNameSelection() {
		if (context != null) {
			int idx = applicationNameCombo.getSelectionIndex();
			if (idx <= -1) {
				return null;
			}
			return applicationInfos[idx];
		}
		return null;
	}

	private URL getBaseURL() throws MalformedURLException {
		if (baseUrl.getText().isEmpty()) {
			return null;
		}
		URL result = new URL(baseUrl.getText());
		return result;
	}

	private boolean isDefaultServer() {
		URL baseUrl = null;
		try {
			baseUrl = getBaseURL();
		} catch (MalformedURLException e) {
			// ignore, handled later
		}
		if (getTarget() != null) {
			URL targetUrl = getTarget().getHost();
			if (baseUrl != null
					&& baseUrl.getHost().equals(targetUrl.getHost())) {
				return true;
			}
		}
		return false;
	}

	private IZendTarget getTarget() {
		return targetsCombo.getSelected();
	}

	private String getInstalledLocation() {
		if (applicationInfos != null && applicationInfos.length > 0) {
			int index = applicationNameCombo.getSelectionIndex();
			if (index != -1) {
				return applicationInfos[index].getInstalledLocation();
			}
		}
		return null;
	}

	private void createDeployCombo(Composite container) {
		targetsCombo.setLabel(Messages.configurationPage_DeployTo);
		targetsCombo.setTooltip(Messages.configurationPage_DeployToTooltip);
		targetsCombo.createControl(container);
		targetsCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				changeHost(targetsCombo.getSelected());
				listener.statusChanged(validatePage());
			}
		});
		targetsCombo.setAddTargetListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				AddTargetAction addTarget = new AddTargetAction();
				addTarget.run();
				IZendTarget newTarget = addTarget.getTarget();
				if (newTarget != null) {
					targetsCombo.updateItems();
					targetsCombo.select(newTarget.getId());
					changeHost(targetsCombo.getSelected());
					listener.statusChanged(validatePage());
				}
			}
		});
	}

	private void createApplicationSelection(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.ConfigurationBlock_ApplicationNameLabel);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		label.setLayoutData(data);
		Composite appsComposite = new Composite(container, SWT.NULL);
		appsComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		appsComposite.setLayout(layout);
		applicationNameCombo = new Combo(appsComposite, SWT.SIMPLE
				| SWT.DROP_DOWN);
		applicationNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		applicationNameCombo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				fillFieldsByAppInfo(applicationNameCombo);
				warnUpdate.setSelection(false);
				warnUpdate.setEnabled(false);
				setBaseURLEnabled(false);
				listener.statusChanged(validatePage());
			}
		});
		applicationNameCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				int matchIndex = isNameMatching(applicationNameCombo.getText());
				if (matchIndex != -1
						&& matchIndex != applicationNameCombo
								.getSelectionIndex()) {
					applicationNameCombo.select(matchIndex);
					fillFieldsByAppInfo(applicationNameCombo);
					warnUpdate.setEnabled(false);
					setBaseURLEnabled(false);
				} else {
					warnUpdate.setEnabled(true);
					setBaseURLEnabled(true);
				}
				listener.statusChanged(validatePage());
			}
		});
		refreshButton = new Button(appsComposite, SWT.PUSH);
		refreshButton.setText(Messages.ConfigurationBlock_RefreshLabel);
		refreshButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		refreshButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				getApplicationsInfo(applicationNameCombo);
				listener.statusChanged(validatePage());
			}
		});
	}

	private int isNameMatching(String name) {
		if (applicationInfos != null && applicationInfos.length > 0) {
			for (int i = 0; i < applicationInfos.length; i++) {
				if (name.equals(applicationInfos[i].getAppName())) {
					return i;
				}
			}
		}
		return -1;
	}

	private void fillFieldsByAppInfo(Combo combo) {
		int index = combo.getSelectionIndex();
		if (index != -1) {
			ApplicationInfo info = applicationInfos[index];
			IDeploymentHelper helper = new DeploymentHelper();
			try {
				helper.setInstalledLocation(info.getInstalledLocation());
				// by default if app is selected then it is update operation
				// helper.setOperationType(getOperationType());
				helper.setAppId(info.getId());
				helper.setAppName(info.getAppName());
				URL baseURL = new URL(info.getBaseUrl());
				if (baseURL.getHost().equals(IDeploymentHelper.DEFAULT_SERVER)) {
					helper.setDefaultServer(true);
					IZendTarget target = getTarget();
					if (target != null) {
						URL updatedURL = new URL(baseURL.getProtocol(), target
								.getHost().getHost(), baseURL.getPath());
						helper.setBaseURL(updatedURL.toString());
					}
				} else {
					helper.setDefaultServer(false);
					helper.setBaseURL(baseURL.toString());
				}
				initializeFields(helper);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
	}

	private void getApplicationsInfo(Combo combo) {
		final IZendTarget selectedTarget = getTarget();
		if (selectedTarget != null && context != null) {
			try {
				context.run(true, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						StatusChangeListener listener = new StatusChangeListener(
								monitor);
						ZendApplication app = new ZendApplication(
								new EclipseMappingModelLoader());
						app.addStatusChangeListener(listener);
						applicationInfos = new ApplicationInfo[0];
						ApplicationsList info = app.getStatus(selectedTarget
								.getId());
						org.zend.webapi.core.progress.IStatus status = listener
								.getStatus();
						StatusCode code = status.getCode();
						if (code == StatusCode.ERROR) {
							StatusManager.getManager().handle(
									new SdkStatus(status), StatusManager.SHOW);
						} else {
							setApplicationsInfos(info);
						}
					}
				});
				populateApplicationsList(combo);
			} catch (InvocationTargetException e) {
				Activator.log(e);
			} catch (InterruptedException e) {
				Activator.log(e);
			}
		}
	}

	private void setApplicationsInfos(ApplicationsList list) {
		if (list != null && list.getApplicationsInfo() != null) {
			applicationInfos = list.getApplicationsInfo().toArray(
					new ApplicationInfo[0]);
		}
	}

	private void populateApplicationsList(final Combo combo) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				String currentSelection = null;
				if (!combo.getText().trim().isEmpty()) {
					currentSelection = combo.getText();
				}
				combo.removeAll();
				int toSelect = -1;
				if (applicationInfos.length != 0) {
					for (int i = 0; i < applicationInfos.length; i++) {
						String name = applicationInfos[i].getAppName();
						combo.add(name);
						if (currentSelection != null
								&& currentSelection.equals(name)) {
							toSelect = i;
						}
					}
				}
				if (currentSelection != null) {
					if (toSelect == -1) {
						combo.setText(currentSelection);
					} else {
						combo.select(toSelect);
						fillFieldsByAppInfo(combo);
					}
				} else {
					combo.select(0);
					fillFieldsByAppInfo(combo);
				}
			}
		});
	}

	private void changeHost(IZendTarget target) {
		URL serverBaseUrl = DeploymentUtils.getServerBaseURL(target);
		URL oldUrl = null;
		try {
			oldUrl = getBaseURL();
		} catch (MalformedURLException e) {
			// ignore, handled later
		}
		try {
			URL updatedUrl = null;
			if (oldUrl == null) {
				updatedUrl = new URL(serverBaseUrl.getProtocol(),
						serverBaseUrl.getHost(), serverBaseUrl.getPort(),
						'/' + applicationNameCombo.getText());
			} else {
				updatedUrl = new URL(serverBaseUrl.getProtocol(),
						serverBaseUrl.getHost(), serverBaseUrl.getPort(),
						oldUrl.getFile());
			}
			baseUrl.setText(updatedUrl.toString());
		} catch (MalformedURLException e) {
			Activator.log(e);
		}
	}

	private List<IDeployWizardContribution> getContributions() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						Activator.WIZARD_CONTRIBUTION_EXTENSION);
		List<IDeployWizardContribution> result = new ArrayList<IDeployWizardContribution>();
		for (IConfigurationElement element : elements) {
			if ("wizardContribution".equals(element.getName())) { //$NON-NLS-1$
				try {
					Object contribution = element
							.createExecutableExtension("class"); //$NON-NLS-1$
					if (contribution instanceof IDeployWizardContribution) {
						result.add((IDeployWizardContribution) contribution);
					}
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		return result;
	}

	private String updateURL(IZendTarget target, String applicationUrl) {
		try {
			URL serverBaseUrl = DeploymentUtils.getServerBaseURL(target);
			URL oldUrl = new URL(applicationUrl);
			URL updatedUrl = new URL(serverBaseUrl.getProtocol(),
					serverBaseUrl.getHost(), serverBaseUrl.getPort(),
					oldUrl.getFile());
			return updatedUrl.toString();
		} catch (MalformedURLException e) {
			Activator.log(e);
		}
		return applicationUrl;
	}

}
