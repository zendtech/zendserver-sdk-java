/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.repository;

/**
 * The IRepositoryLoader is an interface for the repository management system.
 * It allows different environments to load repositories in a different way
 * 
 * @author Roy, 2011
 */
public interface IRepositoryLoader {

	/**
	 * Adds a repository to the system
	 * 
	 * @param repository
	 * @return the repository representation or null in case the repository
	 *         couldn't be added for example if any issue was found during
	 *         persistence operations
	 */
	public IRepository add(IRepository repository);

	/**
	 * @param repository
	 * @return
	 */
	public IRepository remove(IRepository repository);

	/**
	 * Update an existing repository
	 * 
	 * @param repository
	 * @return
	 */
	public IRepository update(IRepository repository);

	/**
	 * Load all repository environments into the system
	 * 
	 * @return IRepository[] repositories
	 */
	public IRepository[] loadAll();
}
