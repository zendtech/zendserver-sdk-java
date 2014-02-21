package org.zend.php.zendserver.deployment.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.PHPLaunchConfigs;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;

public class RemoveLaunchOrTarget extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEvaluationContext ctx = (IEvaluationContext) event.getApplicationContext();
		
		Object element = ctx.getDefaultVariable();
		if (element instanceof List) {
			List<?> list = (List<?>) element;
			for (Object o : list) {
				remove(o);
			}
		}
		
		return null;
	}
	
	private void remove(Object element) {
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration cfg = (ILaunchConfiguration) element;
			try {
				PHPLaunchConfigs.preLaunchConfigurationRemoval(cfg);
				String targetId = cfg.getAttribute(
						DeploymentAttributes.TARGET_ID.getName(), (String) null);
				String projectName = cfg.getAttribute(
						DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
				String baseURL = cfg.getAttribute(
						DeploymentAttributes.BASE_URL.getName(), (String) null);
				cfg.delete();
				if (targetId != null && projectName != null) {
					MonitorManager.removeFilter(targetId, baseURL);
				}
			} catch (CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}
		} else if (element instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) element;
			TargetsManagerService.INSTANCE.removeTarget(target);
			MonitorManager.removeTargetMonitor(target.getId());
		}
		
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		IEvaluationContext ctx = (IEvaluationContext) evaluationContext;
		Object obj = ctx.getDefaultVariable();
		boolean enabled = false;
		if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			if (list.size() > 0) {
				enabled = true;
				for (Object o : list) {
					if (! ((o instanceof IZendTarget) || (o instanceof ILaunchConfiguration))) {
						enabled = false;
					}
				}
			}
		}
		setBaseEnabled(enabled);
	}
	
}
