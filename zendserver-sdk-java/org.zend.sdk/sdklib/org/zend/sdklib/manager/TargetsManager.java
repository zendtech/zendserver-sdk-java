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

import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

/**
 * Target environments manager for the This is a thread-safe class that can be
 * used across threads
 * 
 * @author Roy, 2011
 */
public class TargetsManager {

	/**
	 * All targets loaded in the manager
	 */
	private List<IZendTarget> all = new ArrayList<IZendTarget>(1);

	/**
	 * The mechanism that is responsible to load the targets
	 */
	private final ITargetLoader loader;

	public TargetsManager(ITargetLoader loader) {
		this.loader = loader;

		final IZendTarget[] loadAll = loader.loadAll();
		for (IZendTarget zTarget : loadAll) {
			if (validTarget(zTarget)) {
				throw new IllegalArgumentException(
						"Conflict found when adding " + zTarget.getId());
			}

			this.all.add(zTarget);
		}
	}

	public synchronized IZendTarget add(IZendTarget target) throws WebApiException {
		if (validTarget(target)) {
			throw new IllegalArgumentException("Conflict found when adding "
					+ target.getId());
		}

		// try to connect to server
		if (!target.connect()) {
			return null;
		}

		// notify loader on addition
		this.loader.add(target);

		// adds the target to the list
		final boolean added = this.all.add(target);
		return added ? target : null;
	}

	/**
	 * Check for conflicts and errors in new target
	 * 
	 * @param target
	 * @return
	 */
	private boolean validTarget(IZendTarget target) {
		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}

		if (target.getId() == null) {
			return false;
		}

		for (IZendTarget eTarget : this.all) {
			if (target.getId().equals(eTarget.getId())) {
				return false;
			}

		}
		return true;
	}

	public synchronized IZendTarget remove(IZendTarget target) {
		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}
		if (!this.all.contains(target)) {
			throw new IllegalArgumentException("provided target not found");
		}

		this.loader.remove(target);

		// remove the specified target
		final boolean removed = this.all.remove(target);
		return removed ? target : null;
	}

	/**
	 * Finds a target given target id
	 * 
	 * @param i
	 * @return the specified target
	 */
	public synchronized IZendTarget getTargetById(String id) {
		if (id == null) {
			return null;
		}

		for (IZendTarget target : list()) {
			if (id.equals(target.getId())) {
				return target;
			}
		}

		return null;
	}

	public synchronized IZendTarget[] list() {
		return (IZendTarget[]) this.all
				.toArray(new ZendTarget[this.all.size()]);
	}
}
