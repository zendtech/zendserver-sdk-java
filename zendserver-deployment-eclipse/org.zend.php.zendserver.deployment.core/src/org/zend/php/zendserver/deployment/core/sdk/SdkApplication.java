/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.php.zendserver.deployment.core.sdk;

import org.zend.sdklib.ZendApplication;
import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.library.ILibrary;
import org.zend.webapi.core.connection.data.ApplicationInfo;

public class SdkApplication implements ILibrary {

	private ZendApplication application;

	public SdkApplication() {
		this.application = new ZendApplication();
	}

	public ApplicationInfo deploy(String path, String baseUrl, String targetId,
			String propertiesFile, String appName, Boolean ignoreFailures,
			Boolean createVhost, Boolean defaultServer) {
		return this.application.deploy(path, baseUrl, targetId, propertiesFile,
				appName, ignoreFailures, createVhost, defaultServer);
	}

	public void addStatusChangeListener(IStatusChangeListener listener) {
		application.addStatusChangeListener(listener);
	}

	public void removeStatusChangeListener(IStatusChangeListener listener) {
		application.removeStatusChangeListener(listener);
	}

}
