/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import org.zend.sdklib.target.IZendTarget;

public interface ITargetsManagerListener {

	/**
	 * Method called when a new target is added.
	 * 
	 * @param target
	 *            added target
	 */
	public void targetAdded(IZendTarget target);

	/**
	 * Method called when any of existing targets is removed.
	 * 
	 * @param target
	 */
	public void targetRemoved(IZendTarget target);

}
