package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String mode = event.getParameter(ApplicationContribution.MODE);
		
		Object obj = event.getApplicationContext();
		IEvaluationContext ctx = null;
		if (obj instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) obj;
		}
		
		IProject[] projects = null;
		String targetId = null;
		
		if (ctx != null) {
			projects = getProjects(ctx.getVariable(ApplicationContribution.PROJECT_NAME));
			targetId = (String) ctx.getVariable(ApplicationContribution.TARGET_ID);
		}
		if (projects == null) {
			projects = getProjects(event.getParameter(ApplicationContribution.PROJECT_NAME));
		}
		if (projects == null) {
			projects = new IProject[] { getProjectFromEditor() };
		}
		
		for (IProject project : projects) {
			execute(mode, project, targetId);
		}
		
		return null;
	}

	private void execute(final String mode, IProject project, String targetId) {
		final ILaunchConfiguration config = LaunchUtils.findLaunchConfiguration(project, targetId);
		AbstractLaunchJob deployJob;
		
		if (config == null) {
			DeploymentLaunchDialog dialog = openDeploymentDialog(project, targetId);
			if (dialog == null) {
				return;
			}
			deployJob = new DeployLaunchJob(dialog.getEntry(), project);
		} else {
			DeploymentHelper entry = DeploymentHelper.create(config);
			deployJob = new UpdateLaunchJob(entry, project);
		}
		
		deployJob.setUser(true);
		deployJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				launchApplication(config, (AbstractLaunchJob) event.getJob(), event.getResult().getSeverity(), mode);
			}
		});
		deployJob.schedule();
		
	}

	private void launchApplication(ILaunchConfiguration origConfig, AbstractLaunchJob deployJob, int status, String mode) {
		if (status == IStatus.OK) {
			ILaunchConfiguration config = origConfig == null ? deployJob.getConfig() : origConfig;
			DebugUITools.launch(config, mode);
		}
	}

	private DeploymentLaunchDialog openDeploymentDialog(final IProject project, final String targetId) {
		// TODO pass targetId to the DeploymentLaunchDialog
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		DeploymentLaunchDialog dialog = new DeploymentLaunchDialog(window.getShell(), project);
		if (dialog.open() != Window.OK) {
			return null;
		}
		return dialog;
	}

	private IProject[] getProjects(Object projectName) {
		if (projectName == null) {
			return null;
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		if (projectName instanceof String) {
			IProject project = root.getProject((String)projectName);
			if (project.exists()) {
				return new IProject[] { project };
			}
		}
		
		if (projectName instanceof String[]) {
			List<IProject> projects = new ArrayList<IProject>();
			for (String pName : (String[])projectName) {
				IProject project = root.getProject((String)projectName);
				if (project.exists() && (projects.contains(project))) {
					projects.add(project);
				}
			}
			return projects.toArray(new IProject[projects.size()]);
		}
		
		return null;
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
