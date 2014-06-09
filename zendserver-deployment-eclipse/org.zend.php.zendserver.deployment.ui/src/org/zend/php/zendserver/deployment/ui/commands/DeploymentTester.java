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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Property tester for deployment related commands.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class DeploymentTester extends PropertyTester {

	private static final String PHP_NATURE = "org.eclipse.php.core.PHPNature"; //$NON-NLS-1$

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
				Object phpNature = project.getNature(PHP_NATURE);
				if (phpNature != null) {
					result = true;
				}
			} catch (CoreException e) {
				// Ignore this exception
			}
		}
		return result == (Boolean) expectedValue;
	}
}
