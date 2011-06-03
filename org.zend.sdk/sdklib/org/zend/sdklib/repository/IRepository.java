/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.repository;

import java.io.InputStream;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.repository.site.Application;

/**
 * Interface for clients to access a repository
 * 
 * @author Roy, 2011
 */
public interface IRepository {

	/**
	 * Lists all available applications in the site
	 * 
	 * @return
	 */
	public Application[] getAvailableApplications() throws SdkException;

	/**
	 * Returns a sequence of packages that are required to install an
	 * application
	 * 
	 * @param applicationId
	 * @return
	 */
	public InputStream[] getApplication(String applicationId, String version) throws SdkException;

}
