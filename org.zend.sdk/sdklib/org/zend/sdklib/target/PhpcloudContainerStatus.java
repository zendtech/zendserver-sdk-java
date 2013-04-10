/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.target;

/**
 * List of possible statuses of Phpcloud container.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public enum PhpcloudContainerStatus {

	INITIALIZED("I"),

	PROVISIONED("P"),

	RUNNING("R"),

	SLEEPING("S"),

	FROZEN("F");

	private String status;

	private PhpcloudContainerStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static PhpcloudContainerStatus byName(String name) {
		if (name == null) {
			return null;
		}

		PhpcloudContainerStatus[] values = values();
		for (PhpcloudContainerStatus status : values) {
			if (status.getStatus().equals(name)) {
				return status;
			}
		}

		return null;
	}

}
