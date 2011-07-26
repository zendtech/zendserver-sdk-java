package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.sdk.EclipseMappingModelLoader;
import org.zend.php.zendserver.deployment.core.sdk.SdkStatus;
import org.zend.php.zendserver.deployment.core.sdk.StatusChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentEntry;
import org.zend.php.zendserver.deployment.debug.ui.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.contributions.ApplicationContribution;
import org.zend.php.zendserver.deployment.debug.ui.dialogs.DeploymentLaunchDialog;
import org.zend.sdklib.application.ZendApplication;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;

public class LaunchApplicationHandler extends AbstractHandler {

	private String mode;
	private ILaunchConfiguration config;
	private DeploymentEntry entry;
	private boolean firstTime;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		mode = event.getParameter(ApplicationContribution.MODE);
		final IProject project = getProject(event
				.getParameter(ApplicationContribution.PROJECT_NAME));
		if (project != null) {
			config = LaunchUtils.findLaunchConfiguration(project);
			if (config == null) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				DeploymentLaunchDialog dialog = new DeploymentLaunchDialog(window.getShell(),
						project);
				if (dialog.open() != Window.OK) {
					return null;
				}
				entry = DeploymentEntry.createEntry(dialog, project.getName());
				firstTime = true;
			} else {
				entry = DeploymentEntry.createEntry(config);
				firstTime = false;
			}
			Job deployJob = new Job(firstTime ? Messages.deploymentJob_Title
					: Messages.updateJob_Title) {
				private StatusChangeListener listener;

				public IStatus run(IProgressMonitor monitor) {
					listener = new StatusChangeListener(monitor);
					ZendApplication application = new ZendApplication(
							new EclipseMappingModelLoader());
					application.addStatusChangeListener(listener);
					ApplicationInfo info = null;
					if (firstTime) {
						info = application.deploy(project.getLocation().toString(),
							entry.getBasePath(), entry.getTargetId(), entry.getUserParams(),
							entry.getAppName(), entry.isIgnoreFailures(), entry.getVirtualHost(),
							entry.isDefaultServer());
					} else {
						String appId = String.valueOf(entry.getAppId());
						info = application.update(project.getLocation().toString(),
								entry.getTargetId(), appId, entry.getUserParams(),
								entry.isIgnoreFailures());
					}
					if (monitor.isCanceled()) {
						return Status.OK_STATUS;
					}
					if (info != null && info.getStatus() == ApplicationStatus.STAGING) {
						if (firstTime) {
							try {
								config = LaunchUtils.createConfiguration(project, info.getId(),
										entry);
							} catch (CoreException e) {
								return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
										e.getMessage());
							}
						}
						return monitorApplicationStatus(listener, entry.getTargetId(),
								info.getId(), application, monitor);
					}
					return new SdkStatus(listener.getStatus());
				}
			};
			deployJob.setUser(true);
			deployJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getResult().getSeverity() == IStatus.OK && config != null) {
						DebugUITools.launch(config, mode);
					}
				}
			});
			deployJob.schedule();
		} else {
			DebugUITools.launch(config, mode);
		}
		return null;
	}

	private IStatus monitorApplicationStatus(StatusChangeListener listener, String targetId,
			int id, ZendApplication application, IProgressMonitor monitor) {
		monitor.beginTask(Messages.deploymentStatusJob_Title, IProgressMonitor.UNKNOWN);
		ApplicationStatus result = null;
		while (result != ApplicationStatus.DEPLOYED) {
			if (monitor.isCanceled()) {
				return Status.OK_STATUS;
			}
			ApplicationsList info = application.getStatus(targetId, String.valueOf(id));
			if (info != null && info.getApplicationsInfo() != null) {
				result = info.getApplicationsInfo().get(0).getStatus();
				if (isErrorStatus(result)) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Error on the Zend Server during application deployment: "
									+ result.getName()
									+ "\nTo get more details, see Zend Server log file.");
				}
				monitor.subTask("Current status is: " + result.getName());
			}
		}
		monitor.done();
		return new SdkStatus(listener.getStatus());
	}

	private boolean isErrorStatus(ApplicationStatus result) {
		switch (result) {
		case ACTIVATE_ERROR:
		case DEACTIVATE_ERROR:
		case STAGE_ERROR:
		case UNSTAGE_ERROR:
		case UPLOAD_ERROR:
			return true;
		default:
			return false;
		}
	}

	private IProject getProject(String projectName) {
		if (projectName != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(projectName);
			if (resource != null) {
				return resource.getProject();
			}
		}
		return getProjectFromEditor();
	}

	private IProject getProjectFromEditor() {
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		if (activeEditor != null) {
			IEditorInput editorInput = activeEditor.getEditorInput();
			IFile descriptor = (IFile) editorInput.getAdapter(IFile.class);
			if (descriptor != null) {
				return descriptor.getProject();
			}
		}
		return null;
	}

}
