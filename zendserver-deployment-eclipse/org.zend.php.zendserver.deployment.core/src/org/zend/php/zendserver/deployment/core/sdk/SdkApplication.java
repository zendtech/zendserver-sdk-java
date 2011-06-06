/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.core.sdk;

import java.util.Arrays;

import org.zend.php.zendserver.deployment.core.PreferenceManager;
import org.zend.sdklib.ZendApplication;

public class SdkApplication extends ZendApplication {

	private static final String SEPARATOR = ",";

	public SdkApplication() {
		super(Arrays.asList(getExclusionsPreference()));
	}

	private static String[] getExclusionsPreference() {
		String pref = PreferenceManager.getInstance().getString(
				PreferenceManager.EXCLUDE);
		if (!"".equals(pref)) {
			return pref.split(SEPARATOR);
		}
		return new String[0];
	}

}
