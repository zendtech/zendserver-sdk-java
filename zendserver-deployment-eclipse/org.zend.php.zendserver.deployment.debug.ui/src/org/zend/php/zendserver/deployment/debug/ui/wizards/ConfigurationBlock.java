package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.ui.IAddServerListener;
import org.zend.php.server.ui.ServersCombo;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class ConfigurationBlock extends AbstractBlock {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private ServersCombo targetsCombo = new ServersCombo(true);
	private Text baseUrl;
	private Button ignoreFailures;
	private Button developmentMode;
	private Button warnUpdate;
	private Text applicationNameText;

	private IRunnableContext context;

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
		applicationNameText = createLabelWithText(
				Messages.ConfigurationBlock_ApplicationNameLabel, EMPTY_STRING,
				getContainer(), false);
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
		gd.widthHint = 400;
		devModeDesc.setLayoutData(gd);
		devModeDesc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				org.eclipse.swt.program.Program.launch(e.text);
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
		if (targetId != null) {
			targetsCombo.selectByTarget(targetId);
		}
		developmentMode.setSelection(helper.isDevelopmentModeEnabled());
		warnUpdate.setSelection(helper.isWarnUpdate());
		ignoreFailures.setSelection(helper.isIgnoreFailures());
		applicationNameText.setText(helper.getAppName());
		if (helper.getBaseURL() != null) {
			baseUrl.setText(helper.getBaseURL().toString());
		} else {
			IZendTarget defaultTarget = targetsCombo.getSelectedTarget();
			if (defaultTarget != null) {
				URL targetUrl = defaultTarget.getDefaultServerURL();
				try {
					URL url = new URL(targetUrl.getProtocol(),
							targetUrl.getHost(), targetUrl.getPort(), "/" //$NON-NLS-1$
									+ helper.getAppName());
					baseUrl.setText(url.toString());
				} catch (MalformedURLException e) {
					Activator.log(e);
				}
			}
		}
		for (IDeployWizardContribution c : contributions) {
			c.initializeFields(helper);
		}
	}

	public void clear() {
		baseUrl.setText(EMPTY_STRING);
		ignoreFailures.setSelection(true);
		applicationNameText.clearSelection();
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
		helper.setAppName(applicationNameText.getText());
		helper.setIgnoreFailures(ignoreFailures.getSelection());
		helper.setDefaultServer(isDefaultServer());
		helper.setOperationType(IDeploymentHelper.DEPLOY);
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
		applicationNameText.setEnabled(value);
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
		return targetsCombo.getSelectedTarget();
	}

	private void createDeployCombo(Composite container) {
		targetsCombo.setLabel(Messages.configurationPage_DeployTo);
		targetsCombo.setTooltip(Messages.configurationPage_DeployToTooltip);
		targetsCombo.createControl(container);
		targetsCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				changeHost(targetsCombo.getSelectedTarget());
				listener.statusChanged(validatePage());
			}
		});
		targetsCombo.setListener(new IAddServerListener() {
			public void serverAdded(String name) {
				changeHost(targetsCombo.getSelectedTarget());
				listener.statusChanged(validatePage());
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
						'/' + applicationNameText.getText());
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
