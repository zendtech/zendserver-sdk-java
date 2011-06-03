/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.repository;

import java.io.OutputStream;

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
	public Application[] getAvailableApplications();

	/**
	 * Returns a sequence of packages that are required to install an
	 * application
	 * 
	 * @param applicationId
	 * @return
	 */
	public OutputStream[] getApplication(String applicationId, String version);

}
