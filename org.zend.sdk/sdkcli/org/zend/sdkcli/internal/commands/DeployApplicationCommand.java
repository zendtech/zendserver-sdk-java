/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.ParseError;

/**
 * Deploys application to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class DeployApplicationCommand extends AbstractCommand {

	private static final String PROJECT = "project";
	private static final String PACKAGE = "package";
	private static final String BASE_URL = "baseUrl";
	private static final String TARGET = "target";
	private static final String PARAMS = "name";
	private static final String NAME = "name";
	private static final String IGNORE_FAILURES = "ignoreFailures";
	private static final String CREATE_VHOST = "createVhost";
	private static final String DEFAULT_SERVER = "deafultServer";

	public DeployApplicationCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		// TODO implement execute
		return true;
	}

	@Override
	protected void setupOptions() {
		addArgumentOption(PROJECT, false, "path to the project");
		addArgumentOption(PACKAGE, false, "path to the application package");
		addArgumentOption(BASE_URL, true, "base URL");
		addArgumentOption(TARGET, true, "target ID");
		addArgumentOption(PARAMS, false, "path to parameters properties file");
		addArgumentOption(NAME, false, "use given target name");
		addArgumentOption(IGNORE_FAILURES, false, "use given target name");
		addArgumentOption(CREATE_VHOST, false, "use given target name");
		addArgumentOption(DEFAULT_SERVER, false, "use given target name");
	}
}
