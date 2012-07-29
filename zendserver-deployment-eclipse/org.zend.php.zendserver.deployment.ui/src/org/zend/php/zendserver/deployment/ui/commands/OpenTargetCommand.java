package org.zend.php.zendserver.deployment.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.zend.php.zendserver.deployment.ui.actions.EditTargetAction;
import org.zend.sdklib.target.IZendTarget;

public class OpenTargetCommand extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEvaluationContext ctx = (IEvaluationContext) event.getApplicationContext();
		
		Object element = ctx.getDefaultVariable();
		if (element instanceof List) {
			List<?> list = (List<?>) element;
			if (list.size() > 0) {
				element = list.get(0);
			}
		}
		if (element instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) element;
			new EditTargetAction(new StructuredSelection(target)).run();
		}
		
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		IEvaluationContext ctx = (IEvaluationContext) evaluationContext;
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
