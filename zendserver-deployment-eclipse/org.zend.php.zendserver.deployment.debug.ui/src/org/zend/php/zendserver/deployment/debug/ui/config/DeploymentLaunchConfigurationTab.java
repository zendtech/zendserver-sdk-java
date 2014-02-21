package org.zend.php.zendserver.deployment.debug.ui.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.server.ui.ServerLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.dialogs.DeploymentParametersDialog;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.ConfigurationBlock;

public class DeploymentLaunchConfigurationTab extends AbstractLaunchConfigurationTab implements
		IStatusChangeListener {

	private ConfigurationBlock configBlock;
	private IProject project;
	private Composite container;
	private IDeploymentHelper helper;
	private Map<String, String> currentParameters;
	private Button parametersButton;
	private Button enableDeployment;
	
	private ILaunchConfiguration config;

	public void createControl(Composite parent) {
		final SharedScrolledComposite scrolledComposite = new SharedScrolledComposite(parent,
				SWT.V_SCROLL) {
		};
		scrolledComposite.setLayout(new FillLayout());
		container = new Composite(scrolledComposite, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setContent(container);
		enableDeployment = new Button(container, SWT.CHECK);
		enableDeployment.setText(Messages.DeploymentLaunchConfigurationTab_EnableDeployment);
		enableDeployment.setSelection(false);
		enableDeployment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configBlock.setEnabled(enableDeployment.getSelection());
				parametersButton.setEnabled(enableDeployment.getSelection());
				if (enableDeployment.getSelection()) {
					setDefaultValues();
					ILaunchConfigurationTab[] tabs = getLaunchConfigurationDialog().getTabs();
					for (ILaunchConfigurationTab tab : tabs) {
						if (tab instanceof ServerLaunchConfigurationTab) {
							tab.initializeFrom(config);
						}
					}
				}
				updateLaunchConfigurationDialog();
			}
		});
		String description = null;
		if (ILaunchManager.RUN_MODE.equals(getLaunchConfigurationDialog()
				.getMode())) {
			description = Messages.DeploymentWizard_LaunchDesc;
		} else {
			description = Messages.DeploymentWizard_DebugDesc;
		}
		String projectName = project != null ? project.getName() : null;
		configBlock = new ConfigurationBlock(this, projectName,
				getLaunchConfigurationDialog(), description);
		configBlock.createContents(container, false);
		configBlock.setEnabled(false);
		parametersButton = new Button(container, SWT.PUSH);
		parametersButton.setText(Messages.DeploymentParameters_Title);
		parametersButton.setEnabled(false);
		parametersButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = e.widget.getDisplay().getActiveShell();
				DeploymentParametersDialog dialog = new DeploymentParametersDialog(shell, project,
						helper);
				if (dialog.open() == Window.OK) {
					currentParameters = dialog.getParameters();
					updateLaunchConfigurationDialog();
				}
			}
		});
		setDeploymentPageEnablement(false);
		setControl(scrolledComposite);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	
	@Override
	public void activated(ILaunchConfigurationWorkingCopy configuration) {
		configBlock.setEnabled(false);
		parametersButton.setEnabled(false);
		enableDeployment.setSelection(false);
		helper = DeploymentHelper.create(configuration);
		try {
			project = getProject(configuration);
			if (project == null) {
				configBlock.clear();
				project = LaunchUtils.getProjectFromFilename(configuration);
			} else if (hasDeploymentSupport(project)) {
				enableDeployment.setSelection(true);
				configBlock.clear();
				configBlock.initializeFields(helper);
				configBlock.setEnabled(true);
				setParametersButtonEnabled();
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		this.config = configuration;
		if (getLaunchConfigurationDialog().getActiveTab() == this) {
			try {
				activated(configuration.getWorkingCopy());
			} catch (CoreException e) {
			}
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy wc) {
		if (project != null) {
			IDeploymentHelper updatedHelper = configBlock.getHelper();
			updatedHelper.setProjectName(project.getName());
			updatedHelper.setAppId(helper.getAppId());
			updatedHelper.setInstalledLocation(helper.getInstalledLocation());
			updatedHelper.setOperationType(helper.getOperationType());
			if (currentParameters == null) {
				updatedHelper.setUserParams(helper.getUserParams());
			} else {
				updatedHelper.setUserParams(currentParameters);
			}
			updatedHelper.setEnabled(enableDeployment.getSelection());
			if (helper.equals(updatedHelper)) {
				return;
			}
			helper = updatedHelper;
			if (!enableDeployment.getSelection()) {
				LaunchUtils.removeDeploymentSupport(wc);
			} else if (updatedHelper.getBaseURL() != null) {
				try {
					LaunchUtils.updateLaunchConfiguration(project,
							updatedHelper, wc);
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
		if (!enableDeployment.getSelection()) {
			return true;
		}
		IStatus status = configBlock.validatePage();
		setMessages(status);
		if (status.getSeverity() == IStatus.OK) {
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

	private void setDefaultValues() {
		IDeploymentHelper helper = new DeploymentHelper();
		IDescriptorContainer descContainer = DescriptorContainerManager.getService().openDescriptorContainer(project);
		IDeploymentDescriptor descModel = descContainer.getDescriptorModel();
		String name = descModel.getName();
		if (name == null || name.isEmpty()) {
			name = project.getName();
		}
		enableDeployment.setSelection(helper.isEnabled());
		helper.setProjectName(project.getName());
		helper.setDefaultServer(true);
		helper.setBaseURL("http://default/" + name); //$NON-NLS-1$
		helper.setUserParams(getUserParameters());
		helper.setAppName(name);
		configBlock.initializeFields(helper);
		if (helper.isEnabled()) {
			configBlock.setEnabled(true);
			setParametersButtonEnabled();
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
		configBlock.setApplicationNameEnabled(value);
	}

	private Map<String, String> getUserParameters() {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		Map<String, String> result = new HashMap<String, String>();
		List<IParameter> params = model.getDescriptorModel().getParameters();
		for (IParameter parameter : params) {
			result.put(parameter.getId(), parameter.getDefaultValue());
		}
		return result;
	}

	private void setParametersButtonEnabled() {
		if (helper.getUserParams() == null || helper.getUserParams().size() == 0) {
			parametersButton.setEnabled(false);
		} else {
			parametersButton.setEnabled(true);
		}
	}

	private boolean hasDeploymentSupport(IProject project) {
		IResource descriptor = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		return descriptor != null;
	}

}
