package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class ConfigurationBlock extends AbstractBlock {

	private Combo deployCombo;
	private IZendTarget[] deployComboTargets = new IZendTarget[0];
	private Link targetLink;
	private BaseUrlControl baseUrl;
	private Text userAppName;
	private Button defaultServer;
	private Button ignoreFailures;

	private TargetsManager targetsManager;

	public ConfigurationBlock(IStatusChangeListener context) {
		super(context);
		this.targetsManager = TargetsManagerService.INSTANCE.getTargetManager();
	}

	@Override
	public Composite createContents(Composite parent) {
		super.createContents(parent);
		getContainer().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		createDeployCombo(getContainer());
		createLocationLink(getContainer());
		userAppName = createLabelWithText(Messages.parametersPage_appUserName,
				Messages.parametersPage_appUserNameTooltip, getContainer());
		createBaseUrl(getContainer());
		ignoreFailures = createLabelWithCheckbox(Messages.parametersPage_ignoreFailures,
				Messages.parametersPage_ignoreFailuresTooltip, getContainer());
		return getContainer();
	}

	@Override
	public void initializeFields(IDeploymentHelper helper) {
		if (helper.getBasePath().length() > 0) {
			baseUrl.setURL(helper.getVirtualHost(), helper.getBasePath().substring(1));
		}
		baseUrl.setDefaultServer(helper.isDefaultServer());
		defaultServer.setSelection(helper.isDefaultServer());
		ignoreFailures.setSelection(helper.isIgnoreFailures());
		for (int i = 0; i < deployComboTargets.length; i++) {
			if (deployComboTargets[i].getId() == helper.getTargetId()) {
				deployCombo.select(i);
			}
		}
		userAppName.setText(helper.getAppName());
	}

	@Override
	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_TargetLocation);
		}
		if (!baseUrl.isValid()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.parametersPage_ValidationError_BaseUrl);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.deploymentWizard_Message);
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

	public void setDefaultServerEnabled(boolean value) {
		defaultServer.setEnabled(value);
	}

	public void setIgnoreFailuresEnabled(boolean value) {
		ignoreFailures.setEnabled(value);
	}

	private void createBaseUrl(Composite container) {
		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.parametersPage_baseURL);
		baseUrl = new BaseUrlControl();
		baseUrl.createControl(container);
		baseUrl.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				getContext().statusChanged(validatePage());
			}
		});
		baseUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		defaultServer = createLabelWithCheckbox(Messages.parametersPage_defaultServer,
				Messages.parametersPage_defaultServerTooltip, container);
		defaultServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				baseUrl.setDefaultServer(defaultServer.getSelection());
				getContext().statusChanged(validatePage());
			}
		});
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

}
