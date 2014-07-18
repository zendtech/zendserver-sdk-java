package org.zend.php.zendserver.deployment.debug.ui.config;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.ParameterType;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.ExisitngAppIdJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeployJobChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeploymentHandler {

	private AbstractLaunchJob job;

	private boolean dialogResult;
	private boolean cancelled;

	private DeployJobChangeListener listener;

	private ILaunchConfiguration config;
	private String mode;

	public DeploymentHandler() {
		this(null);
	}

	public DeploymentHandler(ILaunchConfiguration config) {
		super();
		this.config = config;
	}

	public int executeDeployment(String executionMode) {
		mode = executionMode;
		job = null;
		try {
			boolean isRunAs = LaunchUtils.updateConfigForRunAs(config);
			if (isRunAs) {
				return IStatus.OK;
			}
		} catch (CoreException e) {
			Activator.log(e);
			return IStatus.ERROR;
		}
		listener = new DeployJobChangeListener(config);
		try {
			if (LaunchUtils.getConfigurationType() == config.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(config);
				final IProject project = LaunchUtils
						.getProjectFromFilename(config);
				if (!helper.isEnabled()) {
					return IStatus.OK;
				}
				switch (helper.getOperationType()) {
				case IDeploymentHelper.DEPLOY:
					if (helper.getTargetId().isEmpty()) {
						IDeploymentHelper defaultHelper = LaunchUtils
								.createDefaultHelper(project);
						if (defaultHelper != null) {
							try {
								helper = defaultHelper;
								LaunchUtils.updateLaunchConfiguration(project,
										defaultHelper, config.getWorkingCopy());
							} catch (CoreException e) {
								Activator.log(e);
							}
						}
					}
					if (!helper.getTargetId().isEmpty()
							&& !hasEmptyParameters(project, helper)) {
						job = new DeployLaunchJob(helper, project);
					} else {
						setDefaultTarget(helper, project);
						if (helper.getTargetId() != null
								|| !helper.getTargetId().isEmpty()) {
							helper.setProjectName(project.getName());
						}
						doOpenDeploymentWizard(helper, project);
					}
					if (job == null) {
						return IStatus.CANCEL;
					}
					break;
				case IDeploymentHelper.UPDATE:
					if (hasEmptyParameters(project, helper)) {
						setDefaultTarget(helper, project);
						if (helper.getTargetId() != null
								|| !helper.getTargetId().isEmpty()) {
							helper.setProjectName(project.getName());
						}
						doOpenDeploymentWizard(helper, project);
					} else {
						job = new UpdateLaunchJob(helper, project);
					}
					break;
				case IDeploymentHelper.AUTO_DEPLOY:
					job = LaunchUtils.getAutoDeployJob();
					if (job == null) {
						return IStatus.CANCEL;
					}
					job.setHelper(helper);
					job.setProjectPath(project);
					job.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							if (event.getResult().getSeverity() == IStatus.CANCEL) {
								cancelled = true;
							}
						}
					});
					break;
				case IDeploymentHelper.NO_ACTION:
					updateLaunchConfiguration(helper, config, project);
					return IStatus.OK;
				default:
					return IStatus.CANCEL;
				}
				job.addJobChangeListener(listener);
				job.setUser(true);
				job.schedule();
				job.join();
				return verifyJobResult(job.getHelper(), project);
			}
		} catch (CoreException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	public int openNoConfigDeploymentWizard(IDeploymentHelper helper,
			IProject project) {
		job = null;
		try {
			doOpenDeploymentWizard(helper, project);
			if (job == null) {
				return IStatus.OK;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	public int noWizardDeploy(IDeploymentHelper helper, IProject project) {
		try {
			job = new DeployLaunchJob(helper, project);
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	public int openDeploymentWizard() {
		job = null;
		listener = new DeployJobChangeListener(config);
		try {
			if (LaunchUtils.getConfigurationType() == config.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(config);
				final IProject project = LaunchUtils
						.getProjectFromFilename(config);
				doOpenDeploymentWizard(helper, project);
				if (job == null) {
					return IStatus.OK;
				}
				job.addJobChangeListener(listener);
				job.setUser(true);
				job.schedule();
				job.join();
				return verifyJobResult(job.getHelper(), project);
			}
		} catch (CoreException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return IStatus.OK;
	}

	/**
	 * Perform a deployment job base on provided {@link IDeploymentHelper} and a
	 * project.
	 * 
	 * @param helper
	 *            {@link IDeploymentHelper} instance with deployment parameters
	 * @param project
	 *            {@link IProject} instance
	 * @return {@link IStatus#OK} if action was performed successfully;
	 *         otherwise return {@link IStatus#CANCEL}
	 */
	public int performJob(IDeploymentHelper helper, IProject project) {
		switch (helper.getOperationType()) {
		case IDeploymentHelper.DEPLOY:
			job = new DeployLaunchJob(helper, project);
			break;
		case IDeploymentHelper.UPDATE:
			job = new UpdateLaunchJob(helper, project);
			break;
		case IDeploymentHelper.AUTO_DEPLOY:
			job = LaunchUtils.getAutoDeployJob();
			if (job == null) {
				break;
			}
			job.setHelper(helper);
			job.setProjectPath(project);
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().getSeverity() == IStatus.CANCEL) {
						cancelled = true;
					}
				}
			});
			break;
		}
		if (job != null) {
			job.setUser(true);
			job.schedule();
			try {
				job.join();
				return verifyJobResult(job.getHelper(), project);
			} catch (InterruptedException e) {
				Activator.log(e);
			}
		}
		return IStatus.OK;
	}

	private void setDefaultTarget(IDeploymentHelper helper,
			final IProject project) {
		String targetId = helper.getTargetId();
		if (targetId == null || targetId.isEmpty()) {
			IZendTarget defaultTarget = LaunchUtils.getDefaultTarget(project);
			if (defaultTarget != null) {
				helper.setTargetId(defaultTarget.getId());
				helper.setTargetHost(defaultTarget.getHost().toString());
			}
		}
	}

	private boolean hasEmptyParameters(IProject project,
			IDeploymentHelper helper) {
		IResource descriptor = project
				.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		List<IParameter> definedParams = model.getDescriptorModel()
				.getParameters();
		int size = 0;
		for (IParameter param : definedParams) {
			ParameterType type = ParameterType.byName(param.getType());
			if (type != null) {
				size++;
			}
		}
		if (definedParams == null || size == 0) {
			return false;
		}
		Map<String, String> params = helper.getUserParams();
		if (params != null && params.size() > 0) {
			for (IParameter parameter : definedParams) {
				String value = (String) params.get(parameter.getId());
				if (parameter.isRequired()
						&& (value == null || value.isEmpty())) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	private boolean isCancelled() {
		if (listener != null) {
			return listener.isCancelled() || cancelled;
		}
		return cancelled;
	}

	private int verifyJobResult(final IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		if (isCancelled()) {
			return IStatus.CANCEL;
		}
		if (job instanceof DeploymentLaunchJob) {
			DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
			ResponseCode code = deploymentJob.getResponseCode();
			if (code == null) {
				if (helper.getInstalledLocation() == null
						|| helper.getInstalledLocation().trim().isEmpty()) {
					return IStatus.CANCEL;
				}
				if (helper.isDevelopmentModeEnabled()) {
					MonitorManager.addFilter(helper.getTargetId(), helper
							.getBaseURL().toString());
					if (helper.getOperationType() == IDeploymentHelper.DEPLOY
							&& LaunchUtils.isAutoDeployAvailable()) {
						job = getAutoDeployJob(helper, project);
					}
				}
				LaunchUtils.updatePreferences(project, helper.getTargetId(),
						helper.getBaseURL().toString());
			} else if (code == ResponseCode.INVALID_PARAMETER) {
				return handleConflict(helper, project,
						deploymentJob.getResponseCode());
			}
		}
		return IStatus.OK;
	}

	private AbstractLaunchJob getAutoDeployJob(IDeploymentHelper helper,
			IProject project) throws InterruptedException {
		AbstractLaunchJob job = LaunchUtils.getAutoDeployJob();
		if (job == null) {
			// if auto deploy is not available then just leave
			// configuration without changes
			return null;
		}
		helper.setOperationType(IDeploymentHelper.NO_ACTION);
		job.setHelper(helper);
		job.setProjectPath(project);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().getSeverity() == IStatus.CANCEL) {
					cancelled = true;
				}
			}
		});
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setUser(true);
		job.schedule();
		job.join();
		return job;
	}

	private int handleConflict(IDeploymentHelper helper, IProject project,
			ResponseCode code) throws InterruptedException {
		if (helper.isWarnUpdate()) {
			IStatus result = job.getResult();
			if (result.getSeverity() == IStatus.INFO) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						MessageDialog dialog = getUpdateExistingApplicationDialog(Messages.DeploymentHandler_ApplicationNameErrorMessage);
						if (dialog.open() == 0) {
							dialogResult = true;
						}
					}
				});
			}
		} else {
			dialogResult = true;
		}
		if (!dialogResult) {
			doOpenDeploymentWizard(helper, project);
			if (job == null) {
				return IStatus.CANCEL;
			}
			if (listener != null) {
				job.addJobChangeListener(listener);
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} else {
			dialogResult = false;
			job = new ExisitngAppIdJob(helper, project);
			job.setUser(true);
			job.schedule();
			job.join();
			if (job.getHelper().getAppId() != -1) {
				job = new UpdateLaunchJob(job.getHelper(), project);
				if (listener != null) {
					job.addJobChangeListener(listener);
				}
				job.setUser(true);
				job.schedule();
				job.join();
				return verifyJobResult(job.getHelper(), project);
			} else {
				return IStatus.CANCEL;
			}
		}
	}

	private void doOpenDeploymentWizard(final IDeploymentHelper helper,
			final IProject project) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				Mode wizardMode = null;
				if (config != null) {
					if (ILaunchManager.RUN_MODE.equals(mode)) {
						wizardMode = Mode.RUN;
					} else {
						wizardMode = Mode.DEBUG;
					}
				} else {
					wizardMode = Mode.DEPLOY;
				}
				DeploymentWizard wizard = new DeploymentWizard(project, helper,
						wizardMode);
				Shell shell = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				if (dialog.open() == Window.OK) {
					IDeploymentHelper updatedHelper = wizard.getHelper();
					switch (updatedHelper.getOperationType()) {
					case IDeploymentHelper.DEPLOY:
						job = new DeployLaunchJob(updatedHelper, project);
						break;
					case IDeploymentHelper.UPDATE:
						job = new UpdateLaunchJob(updatedHelper, project);
						break;
					case IDeploymentHelper.AUTO_DEPLOY:
						job = LaunchUtils.getAutoDeployJob();
						if (job == null) {
							break;
						}
						job.setHelper(updatedHelper);
						job.setProjectPath(project);
						job.addJobChangeListener(new JobChangeAdapter() {
							@Override
							public void done(IJobChangeEvent event) {
								if (event.getResult().getSeverity() == IStatus.CANCEL) {
									cancelled = true;
								}
							}
						});
						break;
					case IDeploymentHelper.NO_ACTION:
						try {
							if (config != null) {
								updateLaunchConfiguration(updatedHelper,
										config, project);
							}
						} catch (CoreException e) {
							Activator.log(e);
						}
						return;
					}
				} else {
					cancelled = true;
					job = null;
				}
			}
		});
	}

	private void updateLaunchConfiguration(IDeploymentHelper helper,
			final ILaunchConfiguration config, final IProject project)
			throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy) config;
		} else {
			wc = config.getWorkingCopy();
		}
		LaunchUtils.updateLaunchConfiguration(project, helper, wc);
		wc.doSave();
	}

	private MessageDialog getUpdateExistingApplicationDialog(String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		return new MessageDialog(shell,
				Messages.updateExistingApplicationDialog_Title, null, message,
				MessageDialog.QUESTION, new String[] {
						Messages.updateExistingApplicationDialog_yesButton,
						Messages.updateExistingApplicationDialog_noButton }, 1);
	}

}
