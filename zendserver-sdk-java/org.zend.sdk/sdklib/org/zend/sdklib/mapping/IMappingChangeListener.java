/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

/**
 * Interface which allows to listen on changes in a mapping model.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingChangeListener {

	/**
	 * Handles changes fired by model after any modification.
	 * 
	 * @param event
	 */
	void mappingChanged(IMappingChangeEvent event);

}
