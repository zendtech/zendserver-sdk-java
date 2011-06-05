/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.core.sdk;

import java.util.Arrays;
import java.util.HashMap;

import org.zend.php.zendserver.deployment.core.PreferenceManager;
import org.zend.sdklib.ZendApplication;
import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.library.ILibrary;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class SdkApplication implements ILibrary {

	private static final String SEPARATOR = ",";

	private ZendApplication application;

	public SdkApplication() {
		this.application = new ZendApplication(
				Arrays.asList(getExclusionsPreference()));
	}

	public ApplicationInfo deploy(String path, String baseUrl, String targetId,
			HashMap<String, String> userParams, String appName,
			boolean ignoreFailures, boolean createVhost, boolean defaultServer) {
		return application.deploy(path, baseUrl, targetId, userParams, appName,
				ignoreFailures, createVhost, defaultServer);
	}

	public void addStatusChangeListener(IStatusChangeListener listener) {
		application.addStatusChangeListener(listener);
	}

	public void removeStatusChangeListener(IStatusChangeListener listener) {
		application.removeStatusChangeListener(listener);
	}

	private String[] getExclusionsPreference() {
		String pref = PreferenceManager.getInstance().getString(
				PreferenceManager.EXCLUDE);
		if (!"".equals(pref)) {
			return pref.split(SEPARATOR);
		}
		return new String[0];
	}

}
