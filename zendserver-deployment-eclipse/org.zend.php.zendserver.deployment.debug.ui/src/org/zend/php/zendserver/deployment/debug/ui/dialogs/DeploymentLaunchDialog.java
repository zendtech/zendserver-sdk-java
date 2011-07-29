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
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHelper;
import org.zend.sdklib.target.IZendTarget;

public class DeploymentLaunchDialog extends TitleAreaDialog implements IStatusChangeListener {

	private URL baseURL;
	private String userAppName;
	private boolean isDefaultServer;
	private boolean isIgnoreFailures;
	private IZendTarget selectedTarget;
	private Map<String, String> parameters;
	private IProject project;
	private DeploymentConfigurationBlock block;

	public URL getBaseUrl() {
		return baseURL;
	}

	public String getUserAppName() {
		return userAppName;
	}

	public boolean isDefaultServer() {
		return isDefaultServer;
	}

	public boolean isIgnoreFailures() {
		return isIgnoreFailures;
	}

	public IZendTarget getTarget() {
		return selectedTarget;
	}

	public Map<String, String> getParameters() {
		return parameters;
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

	public DeploymentLaunchDialog(Shell parentShell, IProject project) {
		super(parentShell);
		this.project = project;
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
		setTitle(Messages.deploymentDialog_Title);
		setMessage(Messages.deploymentDialog_Message);
		setTitleImage(Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP).createImage());
		block = new DeploymentConfigurationBlock(this);
		Control container = block.createContents(dialogArea);
		block.createParametersGroup(project);
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
		parameters = block.getParameters();
		super.okPressed();
	}

	private void setInitialValues() {
		IDeploymentHelper helper = new DeploymentHelper();
		if (baseURL != null) {
			helper.setBasePath(baseURL.getPath());
			helper.setVirtualHost(baseURL.getHost());
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
	
	public DeploymentHelper getEntry() {
		DeploymentHelper entry = new DeploymentHelper();
		URL baseURL = getBaseUrl();
		String targetId = getTarget().getId();
		entry.setBasePath(baseURL.getPath());
		entry.setProjectName(project.getName());
		entry.setTargetId(targetId);
		entry.setAppId(-1);
		entry.setUserParams(getParameters());
		entry.setAppName(getUserAppName());
		entry.setIgnoreFailures(isIgnoreFailures());
		entry.setDefaultServer(isDefaultServer());
		entry.setVirtualHost(baseURL.getHost());
		return entry;
	}

}
