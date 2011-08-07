package org.zend.php.zendserver.deployment.debug.ui.config;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationBlock;
import org.zend.php.zendserver.deployment.debug.ui.wizards.IStatusChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ParametersBlock;

public class DeploymentLaunchConfigurationTab extends AbstractLaunchConfigurationTab implements
		IStatusChangeListener {

	private ConfigurationBlock configBlock;
	private ParametersBlock parametersBlock;
	private IProject project;

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		configBlock = new ConfigurationBlock(this);
		configBlock.createContents(composite);
		parametersBlock = new ParametersBlock(this);
		parametersBlock.createContents(composite);
		setDeploymentPageEnablement(false);
		setControl(composite);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			project = getProject(configuration);
			if (project == null) {
				project = LaunchUtils.getProjectFromFilename(configuration);
				setDeploymentPageEnablement(true);
			}
			if (project != null) {
				parametersBlock.createParametersGroup(project);
				IDeploymentHelper helper = DeploymentHelper.create(configuration);
				configBlock.initializeFields(helper);
				parametersBlock.initializeFields(helper);
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy wc) {
		if (project != null) {
			URL baseURL = configBlock.getBaseURL();
			IDeploymentHelper helper = new DeploymentHelper();
			if (baseURL != null) {
				helper.setBaseURL(baseURL.toString());
			}
			helper.setTargetId(configBlock.getTarget().getId());
			helper.setProjectName(project.getName());
			helper.setUserParams(parametersBlock.getParameters());
			helper.setAppName(configBlock.getUserAppName());
			helper.setIgnoreFailures(configBlock.isIgnoreFailures());
			helper.setDefaultServer(configBlock.isDefaultServer());
			helper.setOperationType(configBlock.getOperationType());
			try {
				LaunchUtils.updateLaunchConfiguration(project, helper, wc);
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
	}

	public String getName() {
		return Messages.deploymentTab_Title;
	}

	public void statusChanged(IStatus status) {
		setMessages(status);
		updateLaunchConfigurationDialog();
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		IStatus status = configBlock.validatePage();
		setMessages(status);
		if (status.getSeverity() == IStatus.OK) {
			status = parametersBlock.validatePage();
			setMessages(status);
			if (status.getSeverity() == IStatus.OK) {
				return super.isValid(launchConfig);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected void updateLaunchConfigurationDialog() {
		if (getLaunchConfigurationDialog() != null) {
			getLaunchConfigurationDialog().updateButtons();
			getLaunchConfigurationDialog().updateMessage();
		}
	}

	private void setMessages(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			setMessage(status.getMessage());
			setErrorMessage(null);
		} else {
			setErrorMessage(status.getMessage());
		}
	}

	private IProject getProject(ILaunchConfiguration config) throws CoreException {
		String projectName = config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(), "");
		if (!projectName.isEmpty()) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(projectName);
			if (resource != null) {
				return resource.getProject();
			}
		}
		return null;
	}

	private void setDeploymentPageEnablement(boolean value) {
		configBlock.setDeployComboEnabled(value);
		configBlock.setBaseURLEnabled(value);
		configBlock.setUserAppNameEnabled(value);
	}

}
