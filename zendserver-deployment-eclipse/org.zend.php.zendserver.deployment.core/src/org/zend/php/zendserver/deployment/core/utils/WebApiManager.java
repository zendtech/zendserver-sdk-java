/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;

public class WebApiManager {

	private String host = "http://localhost:10081";

	private String keyName = "studio";
	private String secretKey = "d23efe01d8ebc113605b93b7dc1d1281d2dca86216a46ac5d42e521e6d871a52";
	private WebApiClient webApiClient;

	public WebApiManager(String host) {
		super();
		this.host = host;
	}

	public boolean connect() {
		WebApiCredentials credentials = new BasicCredentials(keyName, secretKey);
		try {
			webApiClient = new WebApiClient(credentials, host);
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public ApplicationInfo applicationDeploy(File file, String baseUrl) {
		try {
			ApplicationInfo info = webApiClient
					.applicationDeploy(file, baseUrl);
			if (info != null) {
				return info;
			}
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ApplicationStatus getApplicationStatus(int id) {
		try {
			ApplicationsList list = webApiClient.applicationGetStatus(String
					.valueOf(id));
			List<ApplicationInfo> infos = list.getApplicationsInfo();
			for (ApplicationInfo applicationInfo : infos) {
				if (applicationInfo.getId() == id) {
					return applicationInfo.getStatus();
				}
			}
		} catch (WebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ApplicationStatus.UNKNOWN;
	}

}
