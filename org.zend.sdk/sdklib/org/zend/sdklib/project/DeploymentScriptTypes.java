/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.project;


public enum DeploymentScriptTypes {

	POST_ACTIVATE("postActivate", "post_activate.php"),

	POST_DEACTIVATE("postDeactivate", "post_deactivate.php"),

	POST_STAGE("postStage", "post_stage.php"),

	POST_UNSTAGE("postUnstage", "post_unstage.php"),

	PRE_ACTIVATE("preActivate", "pre_activate.php"),

	PRE_DEACTIVATE("preDeactivate", "pre_deactivate.php"),

	PRE_STAGE("preStage", "pre_stage.php"),

	PRE_UNSTAGE("preUnstage", "pre_unstage.php");

	private final String filename;
	private final String description;

	private DeploymentScriptTypes(String name, String filename) {
		this.description = name;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getDescription() {
		return description;
	}

	public static DeploymentScriptTypes byName(String name) {
		if (name == null) {
			return null;
		}

		DeploymentScriptTypes[] values = values();
		for (DeploymentScriptTypes types : values) {
			if (name.equals(types.getDescription())) {
				return types;
			}
		}
		return null;
	}

	public static DeploymentScriptTypes byFilename(String name) {
		if (name == null) {
			return null;
		}

		DeploymentScriptTypes[] values = values();
		for (DeploymentScriptTypes types : values) {
			if (name.equals(types.getFilename())) {
				return types;
			}
		}
		return null;
	}

}
