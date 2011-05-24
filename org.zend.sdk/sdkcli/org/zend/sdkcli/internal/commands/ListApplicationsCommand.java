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
 * List application statuses.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ListApplicationsCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String APP_ID = "appId";

	public ListApplicationsCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		// TODO implement execute
		return true;
	}

	@Override
	protected void setupOptions() {
		// application ID(s)
		addArgumentsOption(APP_ID, false, "one or more application IDs");
		// target name
		addArgumentOption(ID, true, "use given target name");
	}
}
