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

	CREATE_PROJECT("create", "project", "Creates a new Zend project."),
	
	UPDATE_PROJECT("update", "project", "Updates a Zend project."),

	CLONE_PROJECT("clone", "project", "Clone project from git repository"),

	LIST_TARGETS("list", "targets", "Lists known deployment target environments."),

	ADD_TARGET("add", "target", "Adds new target environment for deploying applications."),
	
	REMOVE_TARGET("remove", "target", "Removes target from the know targets list."),

	DETECT_TARGET("detect", "target", "Detects target on localhost."),
	
	UPDATE_TARGET("update", "target", "Updates target's parameters."),

	LIST_APPLICATIONS("list", "applications", "Lists applications installed on target."),

	DEPLOY_APPLICATION("deploy", "application", "Deploys application to target."),
	
	REDEPLOY_APPLICATION("redeploy", "application", "Re-deploys application on target."),
	
	UPDATE_APPLICATION("update", "application", "Updates application to target."),
	
	REMOVE_APPLICATION("remove", "application", "Removes application from target."),

	DISCOVER_APPLICATION("discover", "application", "discover new application from the repositories list."),
	
	CREATE_PACKAGE("create", "package", "Creates deployment package."),

	ADD_REPOSITORY("add", "repository", "Add repository to the discovery list."),
	
	REMOVE_REPOSITORY("remove", "repository", "Removes repository from the discovery list."),
	
	LIST_REPOSITORIES("list", "repositories", "Show the list of repositories."),
	
	GENERATE_REPOSITORIES("generate", "repository", "generate a repository for a package"),
	
	HELP("help", null, "shows help information.");

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

	public String getInfo() {
		return info;
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

}
