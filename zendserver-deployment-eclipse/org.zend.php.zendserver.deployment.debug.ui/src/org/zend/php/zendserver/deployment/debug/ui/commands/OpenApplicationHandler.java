package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;

public class OpenApplicationHandler extends AbstractDeploymentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ILaunchConfiguration config = getLaunchConfiguration(event.getApplicationContext());
		if (config == null) {
			return null;
		}
		Job job = new Job("Deployment Wizard") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				DeploymentHandler handler = new DeploymentHandler(config);
				if (handler.openDeploymentWizard() == IStatus.OK) {
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}
		};
		job.setSystem(true);
		job.schedule();
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		setBaseEnabled(getLaunchConfiguration(evaluationContext) != null);
	}
	
	private ILaunchConfiguration getLaunchConfiguration(Object evaluationContext) {
		IEvaluationContext ctx = null;
		if (evaluationContext instanceof IEvaluationContext) {
			ctx = (IEvaluationContext) evaluationContext;
		} else {
			return null;
		}
		
		Object object = ctx.getDefaultVariable();
		
		if (object instanceof List<?>) {
			List<?> list = (List<?>) object;
			if (list.size() > 0) {
				object = list.get(0);
			}
		}
		
		if (object instanceof ILaunchConfiguration) {
			return (ILaunchConfiguration) object;
		}
		
		object = ctx.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
		if (object instanceof ISelection) {
			ISelection selection = (ISelection) object; 
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection sselection = (IStructuredSelection) selection;
				object = sselection.getFirstElement();
			}
		}
		
		if (object instanceof ILaunchConfiguration) {
			return (ILaunchConfiguration) object;
		}
		
		return null;
	}
}
