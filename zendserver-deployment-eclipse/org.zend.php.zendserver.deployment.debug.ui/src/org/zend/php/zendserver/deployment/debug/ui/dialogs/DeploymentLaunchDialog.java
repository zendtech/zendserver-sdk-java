package org.zend.php.zendserver.deployment.debug.ui.dialogs;

import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationBlock;
import org.zend.php.zendserver.deployment.debug.ui.wizards.IStatusChangeListener;
import org.zend.sdklib.target.IZendTarget;

public class DeploymentLaunchDialog extends TitleAreaDialog implements IStatusChangeListener {

	private URL baseURL;
	private String userAppName;
	private boolean isDefaultServer;
	private boolean isIgnoreFailures;
	private IZendTarget selectedTarget;
	private Map<String, String> parameters;
	private IProject project;
	private ConfigurationBlock block;

	public DeploymentLaunchDialog(Shell parentShell, IProject project) {
		super(parentShell);
		this.project = project;
	}

	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}

	public void setUserAppName(String userAppName) {
		this.userAppName = userAppName;
	}

	public void setDefaultServer(boolean value) {
		this.isDefaultServer = value;
	}

	public void setIgnoreFailures(boolean value) {
		this.isIgnoreFailures = value;
	}

	public void setTarget(IZendTarget target) {
		this.selectedTarget = target;
	}

	public DeploymentHelper getHelper() {
		DeploymentHelper helper = new DeploymentHelper();
		helper.setBaseURL(baseURL.toString());
		helper.setProjectName(project.getName());
		helper.setTargetId(selectedTarget.getId());
		helper.setAppId(-1);
		helper.setUserParams(parameters);
		helper.setAppName(userAppName);
		helper.setIgnoreFailures(isIgnoreFailures);
		helper.setDefaultServer(isDefaultServer);
		return helper;
	}

	public void statusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			setMessage(status.getMessage());
			setErrorMessage(null);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			setErrorMessage(status.getMessage());
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		setTitle(Messages.deploymentWizard_Title);
		setMessage(Messages.deploymentWizard_Message);
		setTitleImage(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP).createImage());
		block = new ConfigurationBlock(this);
		Control container = block.createContents(dialogArea);
		// block.createParametersGroup(project);
		setInitialValues();
		return container;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control result = super.createButtonBar(parent);
		if (result != null) {
			statusChanged(block.validatePage());
		}
		return result;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setSize(500, 600);
	}

	@Override
	protected void okPressed() {
		baseURL = block.getBaseURL();
		userAppName = block.getUserAppName();
		isDefaultServer = block.isDefaultServer();
		isIgnoreFailures = block.isIgnoreFailures();
		selectedTarget = block.getTarget();
		// parameters = block.getParameters();
		super.okPressed();
	}

	private void setInitialValues() {
		IDeploymentHelper helper = new DeploymentHelper();
		if (baseURL != null) {
			helper.setBaseURL(baseURL.toString());
		}
		if (selectedTarget != null) {
			helper.setTargetId(selectedTarget.getId());
		}
		if (project != null) {
			helper.setProjectName(project.getName());
		}
		if (parameters != null) {
			helper.setUserParams(parameters);
		}
		if (userAppName != null) {
			helper.setAppName(userAppName);
		}
		helper.setIgnoreFailures(isIgnoreFailures);
		helper.setDefaultServer(isDefaultServer);
		block.initializeFields(helper);
	}

}
