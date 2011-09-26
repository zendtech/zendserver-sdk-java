package org.zend.php.zendserver.deployment.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.target.IZendTarget;

public class RemoveTargetCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
		
		Object element = ctx.getDefaultVariable();
		if (element instanceof List) {
			List<?> list = (List<?>) element;
			if (list.size() > 0) {
				element = list.get(0);
			}
		}
		if (element instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) element;
			TargetsManagerService.INSTANCE.getTargetManager().remove(target);
		}
		
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		EvaluationContext ctx = (EvaluationContext) evaluationContext;
		Object obj = ctx.getDefaultVariable();
		if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			if (list.size() > 0) {
				obj = list.get(0);
			}
		}
		setBaseEnabled(obj instanceof IZendTarget);
	}
	
}
