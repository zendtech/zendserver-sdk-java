/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.manager;

import java.util.Arrays;
import java.util.List;

import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.Target;

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
	private List<Target> all;

	/**
	 * The mechanism that is responsible to load the targets
	 */
	private final ITargetLoader loader;

	public TargetsManager(ITargetLoader loader) {
		this.loader = loader;

		this.all = Arrays.asList(loader.loadAll());
	}

	public synchronized Target add(Target target) {
		if (target == null) {
			throw new IllegalArgumentException("target cannot be null");
		}
		// notify loader on addition
		this.loader.add(target);

		// adds the target to the list
		final boolean added = this.all.add(target);
		return added ? target : null;
	}

	public synchronized Target remove(Target target) {
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
	public synchronized Target getTargetById(String id) {
		if (id == null) {
			return null;
		}

		for (Target target : list()) {
			if (id.equals(target.getId())) {
				return target;
			}
		}

		return null;
	}

	public synchronized Target[] list() {
		return (Target[]) this.all.toArray(new Target[this.all.size()]);
	}
}
