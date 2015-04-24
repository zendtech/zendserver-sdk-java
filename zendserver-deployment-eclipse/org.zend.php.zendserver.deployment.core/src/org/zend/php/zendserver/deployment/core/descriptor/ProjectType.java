/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.descriptor;

/**
 * Represents possible project types.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public enum ProjectType {

	LIBRARY("library"), //$NON-NLS-1$

	APPLICATION("application"), //$NON-NLS-1$
	
	UNKNOWN(null);

	private final String name;

	private ProjectType(String name) {
		this.name = name;
	}

	public static ProjectType byName(String name) {
		if (name != null) {
			ProjectType[] values = values();
			for (ProjectType type : values) {
				if (name.equals(type.getName())) {
					return type;
				}
			}
		}
		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

}