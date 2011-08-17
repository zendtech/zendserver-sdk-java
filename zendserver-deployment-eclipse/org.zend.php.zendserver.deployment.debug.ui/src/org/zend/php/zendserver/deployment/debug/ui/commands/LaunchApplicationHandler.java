package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.contributions.ApplicationContribution;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard;
import org.zend.sdklib.target.IZendTarget;

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

		for (IProject project : projects) {
			execute(mode, project, targetId);
		}

		return null;
	}

	private void execute(final String mode, IProject project, String targetId) {
		ILaunchConfiguration config = LaunchUtils.findLaunchConfiguration(project, targetId);
		if (config == null) {
			IDeploymentHelper defaultHelper = null;
			if (targetId != null) {
				defaultHelper = createHelper(targetId, project);
			} else {
				defaultHelper = LaunchUtils.createDefaultHelper(project);
			}
			if (defaultHelper != null) {
				try {
					config = LaunchUtils.createConfiguration(project, defaultHelper);
				} catch (CoreException e) {
					Activator.log(e);
				}
			}
		}
		if (config == null) {
			IDeploymentHelper targetHelper = new DeploymentHelper();
			targetHelper.setTargetId(targetId);
			targetHelper.setProjectName(project.getName());
			DeploymentWizard wizard = new DeploymentWizard(project, targetHelper);
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

	private IDeploymentHelper createHelper(String targetId, IProject project) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager().getTargetById(
				String.valueOf(targetId));
		if (target != null) {
			try {
				IDeploymentHelper helper = new DeploymentHelper();
				URL targetUrl = target.getDefaultServerURL();
				URL baseUrl = new URL(targetUrl.getProtocol(), targetUrl.getHost(),
						targetUrl.getPort(), "/" + project.getName()); //$NON-NLS-1$
				helper.setBaseURL(baseUrl.toString());
				helper.setDefaultServer(true);
				helper.setTargetId(target.getId());
				helper.setTargetHost(target.getHost().getHost().toString());
				helper.setIgnoreFailures(false);
				helper.setOperationType(IDeploymentHelper.DEPLOY);
				helper.setProjectName(project.getName());
				return helper;
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return null;
	}

}
