/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.webapi.core.progress;

/**
 * Represents event which occurs when status of a process is changed.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IStatusChangeEvent {

	/**
	 * @return status which describes actual state of a process
	 */
	IStatus getStatus();

}
