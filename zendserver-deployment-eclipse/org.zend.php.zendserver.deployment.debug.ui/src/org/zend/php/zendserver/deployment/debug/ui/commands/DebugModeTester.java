/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.expressions.PropertyTester;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

public class DebugModeTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) receiver;
			if (TargetsManager.checkMinVersion(target, ZendServerVersion.v6_0_0)) {
				return DebugModeManager.getManager().isInDebugMode(target) == (Boolean) expectedValue;
			}
		}
		return false;
	}

}
