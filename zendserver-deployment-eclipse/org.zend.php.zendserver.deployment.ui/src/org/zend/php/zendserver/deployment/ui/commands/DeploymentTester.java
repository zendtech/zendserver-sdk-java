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
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;

/**
 * Property tester for deployment related commands.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class DeploymentTester extends PropertyTester {

	private static final String QUICK_DEPLOY = "quickDeploy"; //$NON-NLS-1$
	private static final String DEPLOY = "deploy"; //$NON-NLS-1$
	private static final String ADD_SUPPORT = "addSupport"; //$NON-NLS-1$
	private static final String PHP_NATURE = "org.eclipse.php.core.PHPNature"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) receiver;
			Object adapter = (IResource) adaptable.getAdapter(IResource.class);
			if (adapter != null) {
				receiver = adapter;
			}
		}
		IProject project = null;
		if (receiver instanceof IProject) {
			project = (IProject) receiver;
			try {
				Object phpNature = project.getNature(PHP_NATURE);
				if (phpNature == null) {
					return false;
				}
			} catch (CoreException e) {
				// Ignore this exception
			}
		}
		if (receiver instanceof IFile) {
			IFile file = (IFile) receiver;
			if (!DescriptorContainerManager.DESCRIPTOR_PATH.equals(file
					.getName())) {
				return false;
			}
			project = file.getProject();
		}
		boolean result = false;
		if (project != null) {
			IResource file = project
					.getFile((DescriptorContainerManager.DESCRIPTOR_PATH));
			if (ADD_SUPPORT.equals(property)) {
				result = !file.exists();
			} else if (DEPLOY.equals(property)) {
				result = file.exists();
			} else if (QUICK_DEPLOY.equals(property)) {
				result = file.exists();
				if (result == true) {
					result = ServerUtils.getTarget(project) != null;
				}
			}
		}
		return result == (Boolean) expectedValue;
	}
}
