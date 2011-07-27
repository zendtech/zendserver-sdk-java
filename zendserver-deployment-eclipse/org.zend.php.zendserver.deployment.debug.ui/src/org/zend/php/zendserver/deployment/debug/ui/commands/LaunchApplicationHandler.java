package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLaunchJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.UpdateLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.contributions.ApplicationContribution;
import org.zend.php.zendserver.deployment.debug.ui.dialogs.DeploymentLaunchDialog;

public class LaunchApplicationHandler extends AbstractHandler {

	private ILaunchConfiguration config;
	private AbstractLaunchJob deployJob;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String mode = event.getParameter(ApplicationContribution.MODE);
		final IProject project = getProject(event
				.getParameter(ApplicationContribution.PROJECT_NAME));
		if (project != null) {
			config = LaunchUtils.findLaunchConfiguration(project);
			DeploymentHelper entry = null;
			if (config == null) {
				DeploymentLaunchDialog dialog = openDeploymentDialog(project);
				if (dialog == null) {
					return null;
				}
				entry = DeploymentHelper.create(dialog, project.getName());
				deployJob = new DeployLaunchJob(entry, project);
			} else {
				entry = DeploymentHelper.create(config);
				deployJob = new UpdateLaunchJob(entry, project);
			}
			deployJob.setUser(true);
			deployJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					launchApplication(event.getResult().getSeverity(), mode);
				}
			});
			deployJob.schedule();
		}
		return null;
	}

	private void launchApplication(int status, String mode) {
		ILaunchConfiguration newConfig = deployJob.getConfig();
		if (newConfig != null) {
			config = newConfig;
		}
		if (status == IStatus.OK && config != null) {
			DebugUITools.launch(config, mode);
		}
	}

	private DeploymentLaunchDialog openDeploymentDialog(final IProject project) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		DeploymentLaunchDialog dialog = new DeploymentLaunchDialog(window.getShell(), project);
		if (dialog.open() != Window.OK) {
			return null;
		}
		return dialog;
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
