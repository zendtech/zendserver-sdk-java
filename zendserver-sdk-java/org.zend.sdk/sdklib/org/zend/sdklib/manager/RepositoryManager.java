/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.manager;

import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.IRepositoryLoader;
import org.zend.sdklib.repository.RepositoryFactory;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.Site;

/**
 * Manager for user repositories
 * 
 * @author Roy, 2011
 */
public class RepositoryManager extends AbstractChangeNotifier {

	/**
	 * All repositories loaded in the manager
	 */
	private List<IRepository> all = new ArrayList<IRepository>(1);

	/**
	 * The mechanism that is responsible to load the repositories
	 */
	private final IRepositoryLoader loader;

	public RepositoryManager(IRepositoryLoader loader) {
		this.loader = loader;
		final IRepository[] loadAll = loader.loadAll();
		for (IRepository zTarget : loadAll) {
			if (!validRepository(zTarget)) {
				log.error(new IllegalArgumentException(
						"Conflict found when adding " + zTarget.getId()));
			} else {
				this.all.add(zTarget);
			}
		}

	}

	/**
	 * Finds a repository given repository id
	 * 
	 * @param i
	 * @return the specified repository
	 */
	public synchronized IRepository getRepositoryById(String id) {
		if (id == null) {
			return null;
		}

		for (IRepository repository : getRepositories()) {
			if (id.equals(repository.getId())) {
				return repository;
			}
		}

		return null;
	}

	public synchronized IRepository add(IRepository repository)
			throws SdkException {
		if (!validRepository(repository)) {
			return null;
		}

		// try to connect to server
		if (repository.getSite() == null) {
			return null;
		}

		// notify loader on addition
		this.loader.add(repository);

		// adds the repository to the list
		final boolean added = this.all.add(repository);
		return added ? repository : null;
	}

	public synchronized IRepository remove(IRepository repository) {
		if (repository == null) {
			throw new IllegalArgumentException("Repository cannot be null");
		}
		if (!this.all.contains(repository)) {
			throw new IllegalArgumentException("Repository with id '"
					+ repository.getId() + "' does not exist.");
		}

		this.loader.remove(repository);

		// remove the specified repository
		final boolean removed = this.all.remove(repository);
		return removed ? repository : null;
	}

	/**
	 * List all applications in all repositories
	 * 
	 * @return
	 * @throws SdkException
	 */
	public synchronized Application[] listAvailableApplications()
			throws SdkException {
		final IRepository[] repositories = getRepositories();
		List<Application> appls = new ArrayList<Application>(1);
		for (IRepository r : repositories) {
			final Site site = r.getSite();
			appls.addAll(site.getApplication());
		}
		return (Application[]) appls.toArray(new Application[appls.size()]);
	}

	public synchronized IRepository[] getRepositories() {
		return (IRepository[]) this.all
				.toArray(new IRepository[this.all.size()]);
	}

	/**
	 * Creates and adds new repository based on provided parameters.
	 * 
	 * @param url
	 * @param name 
	 * @return
	 */
	public IRepository createRepository(String url, String name) {
		try {
			final IRepository r = RepositoryFactory.createRepository(url, name);
			if (r != null) {
				return r;
			} else {
				log.info("Error adding Zend Repository " + url);
			}
		} catch (SdkException e) {
			log.error("Error adding Zend Target " + url);
			log.error("\tPossible error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Check for conflicts and errors in new target
	 * 
	 * @param repository
	 * @return
	 */
	private boolean validRepository(IRepository repository) {
		if (repository == null) {
			log.error(new IllegalArgumentException("Target cannot be null."));
			return false;
		}
		if (repository.getId() == null) {
			log.error(new IllegalArgumentException(
					"Target is not valid. Target id cannot be null."));
			return false;
		}
		if (getRepositoryById(repository.getId()) != null) {
			log.error("Target with id '" + repository.getId()
					+ "' already exists.");
			return false;
		}
		return true;
	}

}
