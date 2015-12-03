/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.commands;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.php.internal.core.project.PHPNature;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;

/**
 * Property tester for deployment related commands.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DeploymentTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		boolean result = false;
		if (receiver instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) receiver;
			Object adapter = (IResource) adaptable.getAdapter(IResource.class);
			if (adapter != null) {
				receiver = adapter;
			}
		}
		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;
			try {
				if (!project.isAccessible())
					return false;
				
				if(!project.hasNature(PHPNature.ID))
					return false;
				
				if(!project.hasNature(DeploymentNature.ID))
					return false;

				IResource deploymentDescriptor = project.findMember(DescriptorContainerManager.DESCRIPTOR_PATH);
				if(deploymentDescriptor == null)
					return false;
				
				IDescriptorContainer model = DescriptorContainerManager.getService()
						.openDescriptorContainer((IFile) deploymentDescriptor);
				IDeploymentDescriptor descriptor = model.getDescriptorModel();
				ProjectType projectType = descriptor.getType();
				return (projectType == ProjectType.UNKNOWN || projectType == ProjectType.APPLICATION);
				
			} catch (CoreException e) {
				Activator.logError(Messages.DeploymentTester_CouldNotTest_Error, e);
			}
		}
		return result == (Boolean) expectedValue;
	}
}
