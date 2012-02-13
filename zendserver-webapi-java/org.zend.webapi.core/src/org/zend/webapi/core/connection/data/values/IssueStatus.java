/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Represents possible issue states.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public enum IssueStatus {

	OPEN("Open"),

	CLOSED("Closed"),

	IGNORED("Ignored"),

	UNKNOWN("Unknown");

	private final String name;

	private IssueStatus(String name) {
		this.name = name;
	}

	public static IssueStatus byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}

		IssueStatus[] values = values();
		for (IssueStatus issueStatus : values) {
			if (name.equals(issueStatus.getName())) {
				return issueStatus;
			}
		}

		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

}