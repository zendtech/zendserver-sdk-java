package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;
import org.zend.php.zendserver.deployment.debug.ui.contributions.TestingSectionContribution;

public class ProductionDeployApplicationHandler extends AbstractDeploymentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object obj = event.getApplicationContext();
		IEvaluationContext ctx = null;
		if (obj instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) obj;
		}

		IProject[] projects = null;
		String targetId = null;

		if (ctx != null) {
			projects = getProjects(ctx.getVariable(TestingSectionContribution.PROJECT_NAME));
			Object targetIdVariable = ctx.getVariable(TestingSectionContribution.TARGET_ID);
			if (targetIdVariable instanceof String) {
				targetId = (String) targetIdVariable;
			}
		}
		if (projects == null) {
			projects = getProjects(event.getParameter(TestingSectionContribution.PROJECT_NAME));
		}
		if (projects == null) {
			projects = new IProject[] { getProjectFromEditor() };
		}

		for (IProject project : projects) {
			execute(project, targetId);
		}

		return null;
	}

	private void execute(final IProject project, final String targetId) {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return;
		}
		try {
			if (!hasDeploymentNature(project)) {
				Shell shell = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();
				if (MessageDialog.openConfirm(shell,
						Messages.LaunchApplicationHandler_0, Messages.bind(
								Messages.LaunchApplicationHandler_1,
								project.getName()))) {
					enableDeployment(project);
				} else {
					return;
				}
			}
		} catch (CoreException ex) {
			Activator.log(ex);
		}
		Job job = new Job("Deployment Wizard") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				DeploymentHandler handler = new DeploymentHandler();
				IDeploymentHelper defaultHelper = null;
				if (targetId != null) {
					defaultHelper = LaunchUtils.createDefaultHelper(targetId, project);
				} else {
					defaultHelper = LaunchUtils.createDefaultHelper(project);
				}
				defaultHelper.setForProduction(true);
				if (handler.openNoConfigDeploymentWizard(defaultHelper, project) == IStatus.OK) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.setSystem(true);
		job.schedule();
	}

}
