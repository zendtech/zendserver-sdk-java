/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

/**
 * Event which should be fired after any mapping model change.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingChangeEvent {

	/**
	 * Defines kind of change for which the event was thrown.
	 */
	public enum Kind {
		ADD,

		REMOVE,

		MODIFY
	}

	/**
	 * @return {@link Kind}
	 */
	Kind getChangeKind();

	/**
	 * @return mapping
	 */
	IMapping getMapping();

	/**
	 * @return folder
	 */
	String getFolder();

}
