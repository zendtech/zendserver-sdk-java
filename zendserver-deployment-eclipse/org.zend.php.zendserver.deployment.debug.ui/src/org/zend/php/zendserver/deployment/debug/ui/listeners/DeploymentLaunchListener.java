package org.zend.php.zendserver.deployment.debug.ui.listeners;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeploymentLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.NoIdUpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeploymentLaunchListener implements ILaunchDelegateListener {

	private static final int OK = 0;
	private static final int CANCEL = -1;

	private AbstractLaunchJob job;

	private boolean dialogResult = false;
	private boolean cancelled = false;

	public int preLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) {
		job = null;
		try {
			if (LaunchUtils.getConfigurationType() == configuration.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(configuration);
				final IProject project = LaunchUtils.getProjectFromFilename(configuration);
				switch (helper.getOperationType()) {
				case IDeploymentHelper.DEPLOY:
					if (helper.getTargetId().isEmpty()) {
						IDeploymentHelper defaultHelper = LaunchUtils.createDefaultHelper(project);
						if (defaultHelper != null) {
							try {
								helper = defaultHelper;
								LaunchUtils.updateLaunchConfiguration(project, defaultHelper,
										configuration.getWorkingCopy());
							} catch (CoreException e) {
								Activator.log(e);
							}
						}
					}
					if (!helper.getTargetId().isEmpty() && !hasParameters(project)) {
						job = new DeployLaunchJob(helper, project);
					} else {
						openDeploymentWizard(configuration, helper, project);
						if (job == null) {
							return CANCEL;
						}
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
						return CANCEL;
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

	private boolean hasParameters(IProject project) {
		IResource descriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
		IDescriptorContainer model = DescriptorContainerManager.getService()
				.openDescriptorContainer((IFile) descriptor);
		List<IParameter> params = model.getDescriptorModel().getParameters();
		if (params != null && params.size() > 0) {
			return true;
		}
		return false;
	}

	private int verifyJobResult(ILaunchConfiguration config, IDeploymentHelper helper,
			IProject project) throws InterruptedException {
		if (cancelled) {
			return CANCEL;
		}
		if (job instanceof DeploymentLaunchJob) {
			DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
			ResponseCode code = deploymentJob.getResponseCode();
			if (code == null) {
				return OK;
			}
			switch (deploymentJob.getResponseCode()) {
			case BASE_URL_CONFLICT:
				return handleBaseUrlConflict(config, helper, project);
			case APPLICATION_CONFLICT:
				return handleApplicationConflict(config, helper, project);
			default:
				break;
			}
		}
		return OK;
	}
	
	private int handleApplicationConflict(ILaunchConfiguration config, IDeploymentHelper helper,
			IProject project) throws InterruptedException {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				MessageDialog dialog = getApplicationConflictDialog();
				if (dialog.open() != 0) {
					dialogResult = true;
				}
			}
		});
		if (!dialogResult) {
			openDeploymentWizard(config, helper, project);
			if (job == null) {
				return CANCEL;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(config, job.getHelper(), project);
		} else {
			dialogResult = false;
			return CANCEL;
		}
	}

	private int handleBaseUrlConflict(ILaunchConfiguration config, IDeploymentHelper helper,
			IProject project) throws InterruptedException {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				MessageDialog dialog = getUpdateExistingApplicationDialog();
				if (dialog.open() == 0) {
					dialogResult = true;
				}
			}
		});
		if (!dialogResult) {
			openDeploymentWizard(config, helper, project);
			if (job == null) {
				return CANCEL;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(config, job.getHelper(), project);
		} else {
			dialogResult = false;
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
		if (job instanceof DeploymentLaunchJob) {
			DeploymentLaunchJob deploymentJob = (DeploymentLaunchJob) job;
			ResponseCode code = deploymentJob.getResponseCode();
			if (code != null) {
				return;
			}
		}
		try {
			ILaunchConfigurationWorkingCopy wc = null;
			if (config instanceof ILaunchConfigurationWorkingCopy) {
				wc = (ILaunchConfigurationWorkingCopy) config;
			} else {
				wc = config.getWorkingCopy();
			}
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

	private void openDeploymentWizard(final ILaunchConfiguration configuration,
			final IDeploymentHelper helper, final IProject project) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				DeploymentWizard wizard = new DeploymentWizard(project, helper);
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setPageSize(550, 350);
				dialog.create();
				if (dialog.open() == Window.OK) {
					IDeploymentHelper updatedHelper = wizard.getHelper();
					switch (updatedHelper.getOperationType()) {
					case IDeploymentHelper.DEPLOY:
						job = new DeployLaunchJob(updatedHelper, project);
						addJobListener(configuration, project);
						break;
					case IDeploymentHelper.UPDATE:
						job = new UpdateLaunchJob(updatedHelper, project);
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
						return;
					}
				} else {
					job = null;
				}
			}
		});
	}

	private AbstractLaunchJob getAutoDeployJob() {
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(Activator.AUTO_DEPLOY_EXTENSION_ID);
		try {
			for (IConfigurationElement e : config) {

				final Object o = e.createExecutableExtension("class"); //$NON-NLS-1$
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
		return new MessageDialog(shell, Messages.updateExistingApplicationDialog_Title, null,
				Messages.updateExistingApplicationDialog_Message, MessageDialog.QUESTION,
				new String[] { Messages.updateExistingApplicationDialog_yesButton,
						Messages.updateExistingApplicationDialog_noButton }, 1);
	}

	private MessageDialog getApplicationConflictDialog() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		return new MessageDialog(shell, Messages.applicationConflictDialog_Title, null,
				Messages.applicationConflictDialog_Message, MessageDialog.QUESTION, new String[] {
						Messages.updateExistingApplicationDialog_yesButton,
						Messages.updateExistingApplicationDialog_noButton }, 0);
	}

}
