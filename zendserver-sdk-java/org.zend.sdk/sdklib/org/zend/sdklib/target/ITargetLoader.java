/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;


/**
 * The ITargetLoader is an interface for the target management system. It allows
 * different environments to load targets in a different way
 * 
 * @author Roy, 2011
 */
public interface ITargetLoader {

	/**
	 * Adds given <code>target</code> to the system
	 * 
	 * @param target
	 * @return the target representation or null in case the target
	 *         couldn't be added (e.g. if any issue was found during
	 *         persistence operations)
	 */
	public IZendTarget add(IZendTarget target);

	/**
	 * Removes given <code>target</code> from the system
	 * 
	 * @param target
	 * @return the target representation or null in case the target
	 *         couldn't be removed (e.g. if any issue was found during
	 *         persistence operations)
	 */
	public IZendTarget remove(IZendTarget target);

	/**
	 * Updates an existing target
	 * 
	 * @param target
	 * @return the target representation or null in case the target
	 *         couldn't be updated (e.g. if any issue was found during
	 *         persistence operations)
	 */
	public IZendTarget update(IZendTarget target);

	
	/**
	 * Checks if given <code>target</code> is available in the system
	 * 
	 * @param target
	 * @return <code>true</code> if target is available; <code>false</code> otherwise
	 */
	public boolean isAvailable(IZendTarget target);
	
	/**
	 * Loads all target environments into the system
	 * 
	 * @return ITarget[] targets
	 */
	public IZendTarget[] loadAll();
}
