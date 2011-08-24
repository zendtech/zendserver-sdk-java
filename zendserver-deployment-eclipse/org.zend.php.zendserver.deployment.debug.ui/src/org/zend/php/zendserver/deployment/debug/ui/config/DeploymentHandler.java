package org.zend.php.zendserver.deployment.debug.ui.config;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
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
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeployJobChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.webapi.core.connection.response.ResponseCode;

public class DeploymentHandler {

	public static final int OK = 0;
	public static final int CANCEL = -1;

	private AbstractLaunchJob job;

	private boolean dialogResult;
	private boolean cancelled;

	private DeployJobChangeListener listener;
	
	private ILaunchConfiguration config;

	public DeploymentHandler() {
		this(null);
	}

	public DeploymentHandler(ILaunchConfiguration config) {
		super();
		this.config = config;
	}

	public int executeDeployment() {
		job = null;
		listener = new DeployJobChangeListener(config);
		try {
			if (LaunchUtils.getConfigurationType() == config.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(config);
				final IProject project = LaunchUtils.getProjectFromFilename(config);
				switch (helper.getOperationType()) {
				case IDeploymentHelper.DEPLOY:
					if (helper.getTargetId().isEmpty()) {
						IDeploymentHelper defaultHelper = LaunchUtils.createDefaultHelper(project);
						if (defaultHelper != null) {
							try {
								helper = defaultHelper;
								LaunchUtils.updateLaunchConfiguration(project, defaultHelper,
										config.getWorkingCopy());
							} catch (CoreException e) {
								Activator.log(e);
							}
						}
					}
					if (!helper.getTargetId().isEmpty() && !hasParameters(project)) {
						job = new DeployLaunchJob(helper, project);
					} else {
						doOpenDeploymentWizard(helper, project);
					}
					if (job == null) {
						return OK;
					}
					break;
				case IDeploymentHelper.UPDATE:
					job = new UpdateLaunchJob(helper, project);
					break;
				case IDeploymentHelper.AUTO_DEPLOY:
					job = getAutoDeployJob();
					if (job == null) {
						return CANCEL;
					}
					job.setHelper(helper);
					job.setProject(project);
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
					return OK;
				default:
					return CANCEL;
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
		return OK;
	}

	public int openNoConfigDeploymentWizard(IDeploymentHelper helper, IProject project) {
		job = null;
		try {
			doOpenDeploymentWizard(helper, project);
			if (job == null) {
				return OK;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return OK;
	}

	public int openDeploymentWizard() {
		job = null;
		listener = new DeployJobChangeListener(config);
		try {
			if (LaunchUtils.getConfigurationType() == config.getType()) {
				IDeploymentHelper helper = DeploymentHelper.create(config);
				final IProject project = LaunchUtils.getProjectFromFilename(config);
				doOpenDeploymentWizard(helper, project);
				if (job == null) {
					return OK;
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

	private boolean isCancelled() {
		if (listener != null) {
			return listener.isCancelled() && cancelled;
		}
		return cancelled;
	}

	private int verifyJobResult(IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		if (isCancelled()) {
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
				return handleBaseUrlConflict(helper, project);
			case APPLICATION_CONFLICT:
				return handleApplicationConflict(helper, project);
			default:
				break;
			}
		}
		return OK;
	}

	private int handleApplicationConflict(IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				MessageDialog dialog = getApplicationConflictDialog();
				if (dialog.open() != 0) {
					dialogResult = true;
				}
			}
		});
		if (!dialogResult) {
			doOpenDeploymentWizard(helper, project);
			if (job == null) {
				return CANCEL;
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
			return CANCEL;
		}
	}

	private int handleBaseUrlConflict(IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				MessageDialog dialog = getUpdateExistingApplicationDialog();
				if (dialog.open() == 0) {
					dialogResult = true;
				}
			}
		});
		if (!dialogResult) {
			doOpenDeploymentWizard(helper, project);
			if (job == null) {
				return CANCEL;
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
			job = new NoIdUpdateLaunchJob(helper, project);
			if (listener != null) {
				job.addJobChangeListener(listener);
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return OK;
		}
	}

	private void doOpenDeploymentWizard(final IDeploymentHelper helper, final IProject project) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				DeploymentWizard wizard = new DeploymentWizard(project, helper, config != null);
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setPageSize(550, 350);
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
						job = getAutoDeployJob();
						if (job == null) {
							break;
						}
						job.setHelper(updatedHelper);
						job.setProject(project);
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
								updateLaunchConfiguration(updatedHelper, config, project);
							}
						} catch (CoreException e) {
							Activator.log(e);
						}
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

	private void updateLaunchConfiguration(IDeploymentHelper helper,
			final ILaunchConfiguration config, final IProject project) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = null;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			wc = (ILaunchConfigurationWorkingCopy) config;
		} else {
			wc = config.getWorkingCopy();
		}
		LaunchUtils.updateLaunchConfiguration(project, helper, wc);
		wc.doSave();
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
