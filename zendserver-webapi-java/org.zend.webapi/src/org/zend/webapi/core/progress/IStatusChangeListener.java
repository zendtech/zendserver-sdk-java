/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.webapi.core.progress;

/**
 * Represents listener which should listen on changes of a status in a
 * particular process.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IStatusChangeListener {

	/**
	 * This method is called by a library process each time when its status has
	 * changed. It should consist all logic related to change processing.
	 * 
	 * @param event
	 */
	void statusChanged(IStatusChangeEvent event);

}
