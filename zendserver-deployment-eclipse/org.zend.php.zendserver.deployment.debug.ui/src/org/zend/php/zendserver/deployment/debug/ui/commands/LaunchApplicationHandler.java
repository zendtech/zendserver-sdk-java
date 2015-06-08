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
import org.zend.php.zendserver.deployment.debug.ui.contributions.TestingSectionContribution;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode;

public class LaunchApplicationHandler extends AbstractDeploymentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String mode = event.getParameter(TestingSectionContribution.MODE);

		Object obj = event.getApplicationContext();
		IEvaluationContext ctx = null;
		if (obj instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) obj;
		}

		IProject[] projects = null;

		if (ctx != null) {
			projects = getProjects(event.getParameter(TestingSectionContribution.PROJECT_NAME));
			// if projects is null them command is not executed from descriptor editor 
			if (projects == null) {
				Object element = ctx.getDefaultVariable();
				if (element instanceof List) {
					List<?> list = (List<?>) element;
					if (list.size() > 0) {
						element = list.get(0);
					}
				}
				if (element instanceof ILaunchConfiguration) {
					ILaunchConfiguration config = (ILaunchConfiguration) element;
					if (config != null) {
						DebugUITools.launch(config, mode);
						return null;
					}
				}
			}
		}
		if (projects == null) {
			projects = getProjects(event.getParameter(TestingSectionContribution.PROJECT_NAME));
		}
		if (projects == null) {
			projects = new IProject[] { getProjectFromEditor() };
		}

		for (IProject project : projects) {
			execute(mode, project);
		}

		return null;
	}

	private void execute(final String mode, IProject project) {
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

		ILaunchConfiguration config = null;
		IDeploymentHelper defaultHelper = LaunchUtils.createDefaultHelper(project);
		Mode wizardMode = ILaunchManager.RUN_MODE.equals(mode) ? Mode.RUN : Mode.DEBUG;
		DeploymentWizard wizard = new DeploymentWizard(project, defaultHelper, wizardMode);
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if (dialog.open() == Window.OK) {
			try {
				config = LaunchUtils.createConfiguration(project, wizard.getHelper());
			} catch (CoreException e) {
				Activator.log(e);
			}
		}
		if (config != null) {
			DebugUITools.launch(config, mode);
		}
	}

}
