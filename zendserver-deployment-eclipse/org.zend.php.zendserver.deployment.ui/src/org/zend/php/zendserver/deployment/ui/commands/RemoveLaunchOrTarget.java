package org.zend.php.zendserver.deployment.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.target.IZendTarget;

public class RemoveLaunchOrTarget extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
		
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
				cfg.delete();
			} catch (CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}
			
		} else if (element instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) element;
			TargetsManagerService.INSTANCE.removeTarget(target);
		}
		
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		EvaluationContext ctx = (EvaluationContext) evaluationContext;
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
