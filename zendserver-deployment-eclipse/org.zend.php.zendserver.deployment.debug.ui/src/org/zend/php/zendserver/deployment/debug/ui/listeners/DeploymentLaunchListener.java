package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;

public class DeploymentLaunchListener implements ILaunchListener {

	private AbstractLaunchJob job;
	private IDeploymentHelper dialogHelper;

	public void launchRemoved(ILaunch launch) {
	}

	public void launchAdded(final ILaunch launch) {
		try {
			if (LaunchUtils.getConfigurationType() == launch.getLaunchConfiguration().getType()) {
				final ILaunchConfiguration config = launch.getLaunchConfiguration();
				IDeploymentHelper helper = DeploymentHelper.create(config);
				final IProject project = LaunchUtils.getProjectFromFilename(config);
				switch (helper.getOperationType()) {
				case IDeploymentHelper.DEPLOY:
					if (!helper.getTargetId().isEmpty()) {
						job = new DeployLaunchJob(helper, project);
					} else {
						openDeploymentWizard(project, "");
						if (dialogHelper == null) {
							return;
						}
						job = new DeployLaunchJob(dialogHelper, project);
					}
					break;
				case IDeploymentHelper.UPDATE:
					job = new UpdateLaunchJob(helper, project);
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
				job.setUser(true);
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						if (event.getResult().getSeverity() == IStatus.OK) {
							updateLaunchConfiguration(launch, project);
						}
					}
				});
				job.schedule();
				job.join();
			}
		} catch (CoreException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
	}

	public void launchChanged(ILaunch launch) {
	}

	private void updateLaunchConfiguration(final ILaunch launch, final IProject project) {
		ILaunchConfigurationWorkingCopy wc;
		try {
			wc = launch.getLaunchConfiguration().getWorkingCopy();
			IDeploymentHelper helper = job.getHelper();
			LaunchUtils.updateLaunchConfiguration(project, helper, wc);
			wc.setAttribute(DeploymentAttributes.APP_ID.getName(), helper.getAppId());
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

}
