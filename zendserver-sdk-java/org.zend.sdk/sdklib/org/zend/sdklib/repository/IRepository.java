/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.repository;

import java.io.IOException;
import java.io.InputStream;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.Site;

/**
 * Interface for clients to access a repository
 * 
 * @author Roy, 2011
 */
public interface IRepository {

	/**
	 * @return the id of the repository
	 */
	public String getId();
	
	/**
	 * @return the name of the repository
	 */
	public String getName();
	
	/**
	 * @return true if the repository is currently accessible
	 */
	public boolean isAccessible();

	/**
	 * Lists all available applications in the site
	 * 
	 * @return
	 */
	public Site getSite() throws SdkException;

	/**
	 * Connects to a package given application
	 * 
	 * @param application
	 * @return
	 * 
	 * @throws IOException
	 */
	public InputStream getPackage(Application application) throws IOException;

}
