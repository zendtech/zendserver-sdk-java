/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.manager;

import org.zend.sdklib.target.IZendTarget;


/**
 * The ITargetLoader is an interface for the target management system. It allows
 * different environments to load targets in a different way
 * 
 * @author Roy, 2011
 */
public interface ITargetLoader {

	/**
	 * @param target
	 * @return
	 */
	public IZendTarget add(IZendTarget target);

	/**
	 * @param target
	 * @return
	 */
	public IZendTarget remove(IZendTarget target);

	/**
	 * Load all target environments into the system
	 * 
	 * @return ITarget[] targets
	 */
	public IZendTarget[] loadAll();
}
