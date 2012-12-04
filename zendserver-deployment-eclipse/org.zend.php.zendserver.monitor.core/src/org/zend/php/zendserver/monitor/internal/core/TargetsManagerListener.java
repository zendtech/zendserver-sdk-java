/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import org.zend.php.zendserver.deployment.core.targets.ITargetsManagerListener;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;

public class TargetsManagerListener implements ITargetsManagerListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.targets.ITargetsManagerListener
	 * #targetAdded(org.zend.sdklib.target.IZendTarget)
	 */
	public void targetAdded(IZendTarget target) {
		MonitorManager.setDefaultPreferences(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.targets.ITargetsManagerListener
	 * #targetRemoved(org.zend.sdklib.target.IZendTarget)
	 */
	public void targetRemoved(IZendTarget target) {
		MonitorManager.removePreferences(target);
	}

}
