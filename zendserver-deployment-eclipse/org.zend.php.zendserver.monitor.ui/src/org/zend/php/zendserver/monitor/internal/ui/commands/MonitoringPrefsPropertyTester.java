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
package org.zend.php.zendserver.monitor.internal.ui.commands;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.zend.sdklib.target.IZendTarget;

public class MonitoringPrefsPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof List<?>) {
			List<?> list = (List<?>) receiver;
			for (Object object : list) {
				if (object instanceof IZendTarget) {
					return true;
				}
			}
		} else if (receiver instanceof IZendTarget) {
			return true;
		}
		return false;
	}

}
