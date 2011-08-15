package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.debug.core.debugger.launching.ILaunchDelegateListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.NoIdUpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeploymentLaunchListener implements ILaunchDelegateListener {

	private static final int OK = 0;
	private static final int CANCEL = -1;

	private AbstractLaunchJob job;
	private IDeploymentHelper dialogHelper;

	private boolean updateExistingApplication = false;
	private boolean cancelled = false;

	public int preLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) {
		try {
			if (LaunchUtils.getConfigurationType() == configuration.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(configuration);
				final IProject project = LaunchUtils.getProjectFromFilename(configuration);
				switch (helper.getOperationType()) {
				case IDeploymentHelper.DEPLOY:
					if (!helper.getTargetId().isEmpty()) {
						job = new DeployLaunchJob(helper, project);
					} else {
						openDeploymentWizard(project, "");
						if (dialogHelper == null) {
							return CANCEL;
						}
						job = new DeployLaunchJob(dialogHelper, project);
					}
					addJobListener(configuration, project);
					break;
				case IDeploymentHelper.UPDATE:
					job = new UpdateLaunchJob(helper, project);
					addJobListener(configuration, project);
					break;
				case IDeploymentHelper.AUTO_DEPLOY:
					job = getAutoDeployJob();
					if (job == null) {
						break;
					}
					job.setHelper(helper);
					job.setProject(project);
					break;
				default:
					return CANCEL;
				}
				job.setUser(true);
				job.schedule();
				job.join();
				return verifyJobResult(configuration, job.getHelper(), project);
			}
		} catch (CoreException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return OK;
	}

	private int preLaunch(ILaunchConfiguration configuration) {
		return preLaunch(configuration, null, null, null);
	}

	private int verifyJobResult(ILaunchConfiguration config, IDeploymentHelper helper,
			IProject project) {
		if (cancelled) {
			return CANCEL;
		}
		if (job instanceof DeploymentLaunchJob) {
			DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
			// handle base URL conflict
			if (deploymentJob.getResponseCode() == ResponseCode.BASE_URL_CONFLICT) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						MessageDialog dialog = getUpdateExistingApplicationDialog();
						if (dialog.open() == 0) {
							updateExistingApplication = true;
						}
					}
				});
				if (!updateExistingApplication) {
					ILaunchConfigurationWorkingCopy wc;
					try {
						wc = config.getWorkingCopy();
						wc.setAttribute(DeploymentAttributes.TARGET_ID.getName(), "");
						wc.doSave();
					} catch (CoreException e) {
						Activator.log(e);
					}
					return preLaunch(config);
				} else {
					updateExistingApplication = false;
					job = new NoIdUpdateLaunchJob(helper, project);
					addJobListener(config, project);
					job.setUser(true);
					job.schedule();
					try {
						job.join();
					} catch (InterruptedException e) {
						Activator.log(e);
					}
					return OK;
				}
			}
		}
		return OK;
	}

	private void addJobListener(final ILaunchConfiguration config, final IProject project) {
		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
				int status = event.getResult().getSeverity();
				switch (status) {
				case IStatus.OK:
					if (deploymentJob.getResponseCode() == null) {
						updateLaunchConfiguration(config, project);
					}
					break;
				case IStatus.CANCEL:
					cancelled = true;
					break;
				default:
					break;
				}
			}
		});
	}

	private void updateLaunchConfiguration(final ILaunchConfiguration config, final IProject project) {
		ILaunchConfigurationWorkingCopy wc;
		try {
			wc = config.getWorkingCopy();
			IDeploymentHelper helper = job.getHelper();
			LaunchUtils.updateLaunchConfiguration(project, helper, wc);
			if (helper.getOperationType() == IDeploymentHelper.DEPLOY) {
				wc.setAttribute(DeploymentAttributes.OPERATION_TYPE.getName(),
						IDeploymentHelper.UPDATE);
			}
			wc.doSave();
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	private void openDeploymentWizard(final IProject project, final String targetId) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				IDeploymentHelper targetHelper = new DeploymentHelper();
				targetHelper.setTargetId(targetId);
				DeploymentWizard wizard = new DeploymentWizard(project, targetHelper);
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				if (dialog.open() != Window.OK) {
					dialogHelper = null;
				} else {
					dialogHelper = wizard.getHelper();
				}
			}
		});
	}

	private AbstractLaunchJob getAutoDeployJob() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(Activator.AUTO_DEPLOY_EXTENSION_ID);
		try {
			for (IConfigurationElement e : config) {

				final Object o = e.createExecutableExtension("class");
				if (o instanceof AbstractLaunchJob) {
					return (AbstractLaunchJob) o;
				}
			}
		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}
		return null;
	}

	private MessageDialog getUpdateExistingApplicationDialog() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		return new MessageDialog(
				shell,
				"Application Base URL Conflict",
				null,
				"Provided base URL is already in use. Do you want to update already deployed application?",
				MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
	}

}
