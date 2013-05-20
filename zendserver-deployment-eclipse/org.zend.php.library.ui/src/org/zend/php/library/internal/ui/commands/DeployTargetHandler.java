package org.zend.php.library.internal.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.zend.php.library.internal.ui.wizards.LibraryDeploymentUtils;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.target.IZendTarget;

public class DeployTargetHandler extends AbstractHandler {

	private static final String CONTAINER = "container"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String containerName = event.getParameter(CONTAINER);
		IZendTarget target = null;

		if (containerName != null) {
			target = TargetsManagerService.INSTANCE
					.getContainerByName(containerName);
		} else {
			IEvaluationContext ctx = (IEvaluationContext) event
					.getApplicationContext();
			Object element = ctx.getDefaultVariable();
			if (element instanceof List) {
				List<?> list = (List<?>) element;
				if (list.size() > 0) {
					element = list.get(0);
				}
			}
			if (element instanceof IZendTarget) {
				target = (IZendTarget) element;
			}
		}
		if (target == null) {
			return null;
		}
		openWizard(target);
		return null;
	}

	private void openWizard(IZendTarget target) {
		LibraryDeploymentUtils util = new LibraryDeploymentUtils();
		util.openLibraryDeploymentWizard(target);
	}
}
