package org.zend.php.zendserver.deployment.ui.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.zend.php.zendserver.deployment.core.DeploymentNature;

public class EnableDeploymentSupport extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty())
			return null;
		
		Object element = null;
		if (selection instanceof IStructuredSelection) {
			element = ((IStructuredSelection)selection).getFirstElement();
		}
			
		if (element instanceof IAdaptable) {
			IResource res = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
			final IProject project = res.getProject();
			
			Job job = new Job("Enable Deployment Support") {
	
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						addNature(project, DeploymentNature.ID, monitor);
					} catch (CoreException e) {
						return e.getStatus();
					}
					
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		
		
		return null;
	}

	private void addNature(IProject project, String id, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = project.getDescription();
		List<String> natures = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
		if (natures.contains(id)) {
			return;
		}
		
		natures.add(id);
		description.setNatureIds(natures.toArray(new String[natures.size()]));
		project.setDescription(description, monitor);
	}

}
