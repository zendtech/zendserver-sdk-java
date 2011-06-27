/*******************************************************************************
 * Copyright (c) Jun 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.util.Map;
import java.util.Set;

/**
 * Contains the mapping rules
 * 
 * @see ResourceMapper for a facility that translates paths using mapping.
 * 
 */
public interface IResourceMapping {

	/**
	 * @return inclusion map for all folders
	 */
	Map<String, Set<IMapping>> getInclusion();

	/**
	 * @return exclusion map for all folders
	 */
	Map<String, Set<IMapping>> getExclusion();

	/**
	 * @return default exclusion list
	 */
	Set<IMapping> getDefaultExclusion();

}
