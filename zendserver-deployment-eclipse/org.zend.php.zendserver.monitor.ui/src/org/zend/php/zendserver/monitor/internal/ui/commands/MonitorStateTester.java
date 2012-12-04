/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui.commands;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.zend.sdklib.target.IZendTarget;

/**
 * Property tester responsible for evaluating current target monitoring
 * enablement for selected target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitorStateTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof List<?>) {
			List<?> list = (List<?>) receiver;
			for (Object object : list) {
				if (object instanceof IZendTarget) {
					IZendTarget target = (IZendTarget) object;
					IEclipsePreferences prefs = InstanceScope.INSTANCE
							.getNode(org.zend.php.zendserver.monitor.core.Activator.PLUGIN_ID);
					return prefs.getBoolean(target.getId(), false) == (Boolean) expectedValue;
				}
			}
		}
		return false;
	}

}
