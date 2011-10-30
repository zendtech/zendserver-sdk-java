package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.contributions.ApplicationContribution;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode;

public class LaunchApplicationHandler extends AbstractDeploymentHandler {

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
		
		ILaunchConfiguration config = null;
		if (ctx != null) {
			obj = ctx.getDefaultVariable();
			if (obj instanceof List<?>) {
				List<?> list = (List<?>) obj;
				if (list.size() > 0) {
					obj = list.get(0);
					if (obj instanceof ILaunchConfiguration) {
						config = (ILaunchConfiguration) obj;
					}
				}
			}
		}
		if (config != null) {
			DebugUITools.launch(config, mode);
			return null;
		}

		for (IProject project : projects) {
			execute(mode, project, targetId);
		}

		return null;
	}

	private void execute(final String mode, IProject project, String targetId) {
		try {
			if (!hasDeploymentNature(project)) {
				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				if (MessageDialog.openConfirm(shell, Messages.LaunchApplicationHandler_0, Messages.bind(Messages.LaunchApplicationHandler_1, project.getName()))) {
					enableDeployment(project);
				} else {
					return;
				}
			}
		} catch (CoreException ex) {
			Activator.log(ex);
		}
		
		ILaunchConfiguration config = LaunchUtils.findLaunchConfiguration(project, targetId);
		if (config == null) {
			IDeploymentHelper defaultHelper = null;
			if (targetId != null) {
				defaultHelper = LaunchUtils.createDefaultHelper(targetId, project);
			} else {
				defaultHelper = LaunchUtils.createDefaultHelper(project);
			}
			Mode wizardMode = ILaunchManager.RUN_MODE.equals(mode) ? Mode.RUN
					: Mode.DEBUG;
			DeploymentWizard wizard = new DeploymentWizard(project,
					defaultHelper, wizardMode);
			Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setPageSize(550, 350);
			dialog.create();
			if (dialog.open() == Window.OK) {
				try {
					config = LaunchUtils.createConfiguration(project, wizard.getHelper());
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		if (config != null) {
			DebugUITools.launch(config, mode);
		}
	}

}
