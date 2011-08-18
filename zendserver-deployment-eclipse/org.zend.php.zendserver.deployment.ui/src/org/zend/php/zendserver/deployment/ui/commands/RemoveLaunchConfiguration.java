package org.zend.php.zendserver.deployment.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.zendserver.deployment.ui.Activator;

public class RemoveLaunchConfiguration extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
		
		ISelection selection = (ISelection) ctx.getVariable(ISources.ACTIVE_MENU_SELECTION_NAME);
		IStructuredSelection ssel = (IStructuredSelection) selection;
		Object element = ssel.getFirstElement();
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration cfg = (ILaunchConfiguration) element;
			try {
				cfg.delete();
			} catch (CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}
		}
		
		return null;
	}
	
}
