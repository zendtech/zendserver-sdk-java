/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.webapi.core.progress;

/**
 * Status code of a process.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public enum StatusCode {

	ERROR("error"),

	WARNING("warning"),

	STARTING("starting"),

	PROCESSING("processing"),

	STOPPING("stopping"),
	
	UNKNOWN("unknown");

	private final String name;

	private StatusCode(String name) {
		this.name = name;
	}

	public static StatusCode byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		StatusCode[] values = values();
		for (StatusCode status : values) {
			if (name.equals(status.getName())) {
				return status;
			}
		}
		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

}
