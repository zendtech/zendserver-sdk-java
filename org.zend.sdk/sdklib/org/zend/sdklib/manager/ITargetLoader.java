/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.manager;

import org.zend.sdklib.target.ITarget;

public interface ITargetLoader {

	/**
	 * @param target
	 * @return 
	 */
	public ITarget add(ITarget target);

	/**
	 * @param target
	 * @return
	 */
	public ITarget remove(ITarget target);

	/**
	 * @param target
	 * @return
	 */
	public ITarget update(ITarget target);

}
