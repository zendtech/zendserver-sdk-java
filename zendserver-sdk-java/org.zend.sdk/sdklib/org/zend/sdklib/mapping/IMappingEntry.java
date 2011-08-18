/*******************************************************************************
 * Copyright (c) Jul 3, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.util.List;

/**
 * Represents entry in the resource mapping.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingEntry {

	public enum Type {

		INCLUDE,

		EXCLUDE

	}

	/**
	 * @return entry folder name
	 */
	String getFolder();

	/**
	 * @return list of mappings for the entry
	 */
	List<IMapping> getMappings();

	/**
	 * @return type of the entry
	 */
	Type getType();

}
