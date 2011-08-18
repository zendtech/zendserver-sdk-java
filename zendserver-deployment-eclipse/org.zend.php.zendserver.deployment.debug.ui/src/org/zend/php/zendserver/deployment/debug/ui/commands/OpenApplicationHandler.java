package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;

public class OpenApplicationHandler extends AbstractDeploymentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object obj = event.getApplicationContext();
		IEvaluationContext ctx = null;
		if (obj instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) obj;
		}

		ISelection selection = (ISelection) ctx.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Object object = sselection.getFirstElement();
		ILaunchConfiguration config = (ILaunchConfiguration) object;
		DeploymentHandler handler = new DeploymentHandler(config);
		handler.openDeploymentWizard();
		
		return null;
	}
}
