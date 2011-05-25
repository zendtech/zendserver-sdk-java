/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib;

import java.net.MalformedURLException;

import org.zend.sdklib.internal.library.AbstractLibrary;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.ApplicationsList;

/**
 * Utility class which provides methods to perform operations on application.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ZendApplication extends AbstractLibrary {

	private final TargetsManager manager;

	public ZendApplication() {
		super();
		manager = new TargetsManager(new UserBasedTargetLoader());
	}

	/**
	 * Provides information about status of specified application(s) in selected
	 * target.
	 * 
	 * @param targetId
	 * @param applicationIds
	 * @return instance of {@link ApplicationsList} or <code>null</code> if
	 *         there where problems with connections or target with specified id
	 *         does not exist.
	 */
	public ApplicationsList getStatus(String targetId, String... applicationIds) {
		try {
			WebApiClient client = getClient(targetId);
			applicationIds = applicationIds == null ? new String[0]
					: applicationIds;
			return client.applicationGetStatus(applicationIds);
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (WebApiException e) {
			log.error("Cannot connect to target '" + targetId + "'.");
			log.error("\tpossible error " + e.getMessage());
		}
		return null;
	}

	/**
	 * @param targetId
	 * @return instance of a WebAPI client for specified target id. If target
	 *         does not exist, it returns <code>null</code>
	 * @throws MalformedURLException
	 */
	public WebApiClient getClient(String targetId) throws MalformedURLException {
		IZendTarget target = manager.getTargetById(targetId);
		if (target == null) {
			log.info("Target with id '" + targetId + "' does not exist.");
			return null;
		}
		WebApiCredentials credentials = new BasicCredentials(target.getKey(),
				target.getSecretKey());
		return new WebApiClient(credentials, target.getHost().toString()
				+ ":10081");
	}

}
