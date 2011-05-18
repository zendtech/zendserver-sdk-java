/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.library;

import org.zend.sdklib.event.IStatusChangeListener;

/**
 * Represents Zend SDK library. Library can consist one or more processes which
 * can be performed.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface ILibrary {

	/**
	 * Registers specified status change listener for this library.
	 * 
	 * @param listener
	 */
	void addStatusChangeListener(IStatusChangeListener listener);

	/**
	 * Unregisters specified status change listener for this library.
	 * 
	 * @param listener
	 */
	void removeStatusChangeListener(IStatusChangeListener listener);

}
