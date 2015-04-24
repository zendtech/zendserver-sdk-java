/*******************************************************************************
 * Copyright (c) Apr 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Application status code.
 * 
 * @author Wojtek, 2011
 * 
 */
public enum ApplicationStatus {

	UPLOADING("uploading"),

	UPLOADED("uploaded"),

	UPLOAD_ERROR("uploadError"),

	STAGING("staging"),

	STAGED("staged"),

	STAGE_ERROR("stageError"),

	ACTIVATING("activating"),

	DEPLOYED("deployed"),

	ACTIVATE_ERROR("activateError"),

	DEACTIVATING("deactivating"),

	DEACTIVATE_ERROR("deactivateError"),

	UNSTAGING("unstaging"),

	UNSTAGE_ERROR("unstageError"),

	UNKNOWN("unknown"),

	INCONSISTENT("inconsistent"),
	
	ROLLING_BACK("rollingBack"),

	NOT_EXISTS("notExists");

	private final String name;

	private ApplicationStatus(String name) {
		this.name = name;
	}

	public static ApplicationStatus byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		ApplicationStatus[] values = values();
		for (ApplicationStatus serverStatus : values) {
			if (name.equals(serverStatus.getName())) {
				return serverStatus;
			}
		}
		return UNKNOWN;
	}

	public String getName() {
		return name;
	}

}