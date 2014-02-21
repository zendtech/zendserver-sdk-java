/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;

/**
 * Implementors can provide additional operation and associate them with
 * deployment process.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public interface IDeploymentContribution {

	IStatus performOperation(IProgressMonitor monitor, IDeploymentHelper helper);
	
}
