/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.ui.wizards.ProjectDeploymentWizard;


public class ApplicationDeployHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IProject currentProject = null;
		if (selection instanceof IStructuredSelection
				&& !((IStructuredSelection) selection).isEmpty()) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();
			if (element instanceof IAdaptable) {
				Object obj = ((IAdaptable) element).getAdapter(IResource.class);
				if (obj != null) {
					element = obj;
				}
			}
			
			if (element instanceof IProject) {
				currentProject = (IProject) element;
			}
			
			if (element instanceof IFile) {
				currentProject = ((IFile) element).getProject();
			}
		}
		if (currentProject != null) {
			IDescriptorContainer model = DescriptorContainerManager
					.getService().openDescriptorContainer(currentProject);
			ProjectDeploymentWizard wizard = new ProjectDeploymentWizard(model);
			WizardDialog dialog = new WizardDialog(
					HandlerUtil.getActiveShell(event), wizard);
			dialog.setPageSize(400, 500);
			return dialog.open();
		}
		return false;
	}

}
