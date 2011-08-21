package org.zend.php.zendserver.deployment.debug.ui.config;

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
		configBlock = new ConfigurationBlock(this, getLaunchConfigurationDialog());
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
				configBlock.clear();
				configBlock.initializeFields(new DeploymentHelper());
				setDeploymentPageEnablement(true);
				project = LaunchUtils.getProjectFromFilename(configuration);
				if (project != null) {
					IDeploymentHelper helper = new DeploymentHelper();
					helper.setProjectName(project.getName());
					helper.setDefaultServer(true);
					helper.setBaseURL("http://default/" + project.getName()); //$NON-NLS-1$
					parametersBlock.createParametersGroup(project);
					configBlock.initializeFields(helper);
					parametersBlock.initializeFields(helper);
				}
			} else {
				configBlock.clear();
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
			IDeploymentHelper helper = configBlock.getHelper();
			helper.setProjectName(project.getName());
			helper.setUserParams(parametersBlock.getHelper().getUserParams());
			if (helper.getBaseURL() != null) {
				try {
					LaunchUtils.updateLaunchConfiguration(project, helper, wc);
				} catch (CoreException e) {
					Activator.log(e);
				}
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

	private void setMessages(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			setMessage(status.getMessage());
			setErrorMessage(null);
		} else {
			setErrorMessage(status.getMessage());
		}
	}

	private IProject getProject(ILaunchConfiguration config) throws CoreException {
		String projectName = config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(), ""); //$NON-NLS-1$
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
