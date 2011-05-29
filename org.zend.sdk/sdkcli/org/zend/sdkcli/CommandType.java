/*******************************************************************************
 * Copyright (c) May 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;

/**
 * Enum which values represent command types.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public enum CommandType {

	CREATE_PROJECT("create", "project", "creates project"),
	
	UPDATE_PROJECT("update", "project", "updates project"),

	LIST_TARGETS("list", "targets", "lists known deployment target environments"),

	CREATE_TARGET("create", "target", "adds new target environment for deploying applications"),
	
	DELETE_TARGET("delete", "target", "removes target from the know targets list"),

	DETECT_TARGET("detect", "target", "detects target on localhost"),
	
	UPDATE_TARGET("update", "target", "updates target's parameters"),

	LIST_APPLICATIONS("list", "applications", "lists applications installed on target"),

	DEPLOY_APPLICATION("deploy", "application", "deploys application to target"),
	
	REDEPLOY_APPLICATION("redeploy", "application", "re-deploys application on target"),
	
	UPDATE_APPLICATION("update", "application", "updates application to target"),
	
	REMOVE_APPLICATION("remove", "application", "removes application from target"),
	
	CREATE_PACKAGE("create", "package", "creates deployment package"),

	HELP("help", null, "shows help information");

	private String verb;
	private String directObject;
	private String info;

	private CommandType(String verb, String directObject, String info) {
		this.verb = verb;
		this.directObject = directObject;
		this.info = info;
	}

	private CommandType(String verb, String directObject) {
		this.verb = verb;
		this.directObject = directObject;
	}
	
	private CommandType(String verb) {
		this(verb, null, null);
	}

	public String getDirectObject() {
		return directObject;
	}

	public String getVerb() {
		return verb;
	}

	/**
	 * Creates CommandTypes instance based on first two command line arguments.
	 * 
	 * @param
	 * @return CommandTypes instance
	 */
	public static CommandType byCommandLine(CommandLine line) {
		String verb = line.getVerb();
		String directObject = line.getDirectObject();
		if (verb == null && directObject == null) {
			return HELP;
		}
		CommandType[] values = values();
		for (CommandType type : values) {
			if (verb.equals(type.getVerb())) {
				if (type.getDirectObject() != null) {
					if (type.getDirectObject().equals(directObject)) {
						return type;
					}
				} else if (directObject == null) {
					return type;
				}
			}
		}
		return HELP;
	}

	public String getInfo() {
		return info;
	}

}
