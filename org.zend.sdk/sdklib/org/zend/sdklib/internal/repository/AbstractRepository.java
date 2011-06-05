/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.repository;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.VersionsUtility;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;

/**
 * Abstract Repository client - Finds for updates according to application
 * listing
 * 
 * @author Roy, 2011
 */
public abstract class AbstractRepository implements IRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.repository.IRepository#getApplicationUpdates(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public Application getApplicationUpdates(String applicationName,
			String currentVersion) throws SdkException {

		final Application[] applications = getApplications();
		for (Application application : applications) {
			if (application.getName().equalsIgnoreCase(applicationName)
					&& validUpdate(currentVersion, application.getVersion(),
							application.getUpdateRange())) {
				return application;
			}
		}

		return null;
	}

	/**
	 * There is an update if <br/>
	 * 1. current version is smaller than new version on site <br/>
	 * 2. current version is in between the range of the update<br/>
	 * 
	 * @param currentVersion
	 * @param newVersion
	 * @param range
	 * @return
	 */
	private boolean validUpdate(String currentVersion, String newVersion,
			String range) {

		final boolean hasUpdate = VersionsUtility.versionCompare(newVersion,
				currentVersion) > 0;
		final boolean inBetween = VersionsUtility.inBetween(currentVersion,
				range);

		return hasUpdate && inBetween;
	}

}
