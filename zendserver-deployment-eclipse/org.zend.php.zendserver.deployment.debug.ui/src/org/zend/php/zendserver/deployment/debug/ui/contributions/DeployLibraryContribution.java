/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployLibraryContribution extends TestingSectionContribution {

	protected static final String DEPLOY_LIBRARY_COMMAND = "org.zend.php.zendserver.deployment.debug.ui.deployLibary"; //$NON-NLS-1$

	public DeployLibraryContribution() {
		super(DEPLOY_LIBRARY_COMMAND, ILaunchManager.RUN_MODE,
				Messages.DeployLibraryContribution_Title, Activator
						.getImageDescriptor(Activator.IMAGE_DEPLOY_LIBRARY));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.ui.contributions.
	 * ITestingSectionContribution#getType()
	 */
	public ProjectType getType() {
		return ProjectType.LIBRARY;
	}

}
